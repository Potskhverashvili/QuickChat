package com.example.quickchat.data.repository

import android.util.Log
import android.util.Log.d
import com.example.quickchat.core.FirebaseCallHelper
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private var database: FirebaseDatabase
) : ChatRepository {
    private var chatRef: DatabaseReference? = null

    // Reference to the "messages" node in the database

    override suspend fun createOrGetChatSession(
        currentUserUid: String,
        otherUserUid: String
    ): OperationStatus<String> {
        return FirebaseCallHelper.safeFirebaseCall {
            // Keep the hardcoded Firebase URL
            val firebaseDatabase = FirebaseDatabase
                .getInstance("https://quickchat-d765e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("messages")

            // Generate a unique chatId by sorting the user IDs alphabetically
            val chatId = listOf(currentUserUid, otherUserUid).sorted().joinToString("_")

            // Reference to the chat in the "messages" collection using the generated chatId
            val chatRef = firebaseDatabase.child(chatId)

            // Check if the chat session exists
            val chatSnapshot = chatRef.get().await()
            Log.d(
                "ChatRepository",
                "Checking if chat exists for chatId: $chatId, exists: ${chatSnapshot.exists()}"
            )

            if (!chatSnapshot.exists()) {
                // If the chat doesn't exist, create a new one
                val chatData = mapOf(
                    "chatId" to chatId,
                    "users" to listOf(currentUserUid, otherUserUid),
                    "createdAt" to System.currentTimeMillis(),
                    "generalMessages" to mutableListOf<MessageModel>() // Initialize generalMessages as an empty list
                )

                // Set the value in the database
                chatRef.setValue(chatData).await()

                // Log that generalMessages is created
                Log.d(
                    "ChatRepository",
                    "New chat session created with generalMessages initialized for chatId: $chatId"
                )
            } else {
                // Log that the chat session already exists
                Log.d("ChatRepository", "Chat session already exists for chatId: $chatId")
            }

            // Return the chatId (either new or existing)
            chatId
        }
    }


    override suspend fun sendMessage(
        chatId: String,
        senderEmail: String,
        senderUid: String,
        text: String
    ) {
        FirebaseCallHelper.safeFirebaseCall {
            val firebaseDatabase = FirebaseDatabase
                .getInstance("https://quickchat-d765e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("messages")

            val chatRef = firebaseDatabase.child(chatId)

            val newMessageId = chatRef.child("generalMessages").push().key

            val newMessage = MessageModel(
                id = newMessageId,
                senderEmail = senderEmail,
                text = text,
                senderUid = senderUid,
                timestamp = System.currentTimeMillis()
            )

            Log.d("SendMessage", "Sending message: $newMessage")

            newMessageId?.let {
                chatRef.child("generalMessages").child(it).setValue(newMessage).await()
            }

            Log.d("SendMessage", "Message sent successfully, fetching all messages.")

            val messagesSnapshot = chatRef.child("generalMessages").get().await()

            Log.d("SendMessage", "Fetched messages: ${messagesSnapshot.children.count()}")
        }
    }

    override suspend fun listenForMessages(chatId: String, onMessageReceived: (MessageModel) -> Unit) {
        d("listenerCheck", "Repository listenForMessages")
        val firebaseDatabase = FirebaseDatabase
            .getInstance("https://quickchat-d765e-default-rtdb.europe-west1.firebasedatabase.app/")
        chatRef = firebaseDatabase.getReference("messages")
            .child(chatId)
            .child("generalMessages")

        chatRef?.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                d("listenerCheck", "fun onChildAdded")
                snapshot.getValue(MessageModel::class.java)?.let { message ->
                    d("listenerCheck", "snapshot.getValueResult: -> $message ")
                    onMessageReceived(message) // Send message to use case
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) = Unit
            override fun onChildRemoved(snapshot: DataSnapshot) = Unit
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) = Unit
            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

}