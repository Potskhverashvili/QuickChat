package com.example.quickchat.data.repository

import android.util.Log
import com.example.quickchat.core.FirebaseCallHelper
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private var database: FirebaseDatabase
) : ChatRepository {

    // Reference to the "messages" node in the database

    override suspend fun createOrGetChatSession(
        currentUserUid: String,
        otherUserUid: String
    ): OperationStatus<String> {
        return FirebaseCallHelper.safeFirebaseCall {
            val firebaseDatabase = FirebaseDatabase
                .getInstance("https://quickchat-d765e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("messages")
            // Generate a unique chatId by sorting the user IDs alphabetically
            val chatId = listOf(currentUserUid, otherUserUid).sorted().joinToString("_")

            // Reference to the chat in the "messages" collection using the generated chatId
            val chatRef = firebaseDatabase.child(chatId)

            // Check if the chat session exists
            val chatSnapshot = chatRef.get().await()
            if (!chatSnapshot.exists()) {
                // If the chat doesn't exist, create a new one
                val chatData = mapOf(
                    "chatId" to chatId,
                    "users" to listOf(currentUserUid, otherUserUid),
                    "createdAt" to System.currentTimeMillis(),
                    "generalMessages" to mutableListOf<MessageModel>()
                )
                // Set the value in the database
                chatRef.setValue(chatData).await()
            }

            // Return the chatId (either new or existing)
            chatId
        }
    }

    override suspend fun sendMessage(
        chatId: String,
        senderEmail: String,
        text: String,
    ): OperationStatus<Unit> {
        return FirebaseCallHelper.safeFirebaseCall {
            val messageId = System.currentTimeMillis().toString() // Create a unique message ID
            val message = mapOf(
                "id" to messageId,
                "text" to text,
                "senderEmail" to senderEmail,
                "timestamp" to System.currentTimeMillis(),
            )

            val firebaseDatabase = FirebaseDatabase
                .getInstance("https://quickchat-d765e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("messages")

            val chatRef = firebaseDatabase.child(chatId)

            // Check if the 'generalMessages' field exists, if not, initialize it
            val chatSnapshot = chatRef.get().await()
            if (!chatSnapshot.child("generalMessages").exists()) {
                chatRef.child("generalMessages").setValue(mutableListOf<MessageModel>()).await()
            }

            // Add the message to the 'generalMessages' node
            val messagesRef = chatRef.child("generalMessages").child(messageId)
            messagesRef.setValue(message).await()

            // Return the result
            Unit
        }
    }


    override suspend fun getMessages(chatId: String): OperationStatus<List<MessageModel>> {
        return FirebaseCallHelper.safeFirebaseCall {
            // Initialize the Firebase Database reference explicitly
            val firebaseDatabase = FirebaseDatabase
                .getInstance("https://quickchat-d765e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("messages")

            // Log the initialized reference path
            Log.d(
                "FirebaseDebug",
                "Firebase Database initialized with path: ${firebaseDatabase.path}"
            )

            val chatRef = firebaseDatabase.child(chatId).child("generalMessages")

            // Log the reference path to ensure it's correct
            Log.d("FirebaseDebug", "Fetching messages from: ${chatRef.path}")

            // Fetch all messages under 'generalMessages' node
            try {
                val messagesSnapshot = chatRef.get().await()

                // Log the number of messages retrieved
                Log.d("FirebaseDebug", "Messages fetched: ${messagesSnapshot.childrenCount}")

                // Convert Firebase data snapshot into a list of MessageModel objects
                val messagesList = mutableListOf<MessageModel>()
                for (snapshot in messagesSnapshot.children) {
                    Log.d(
                        "FirebaseDebug",
                        "Snapshot key: ${snapshot.key}, value: ${snapshot.value}"
                    )
                    val message = snapshot.getValue(MessageModel::class.java)
                    if (message != null) {
                        // Log the message content
                        Log.d("FirebaseDebug", "Message retrieved: ${message.text}")
                        messagesList.add(message)
                    } else {
                        // Log when a message could not be parsed
                        Log.d("FirebaseDebug", "Message parsing failed")
                    }
                }

                // Log the final list of messages
                Log.d("FirebaseDebug", "Total messages: ${messagesList.size}")

                return@safeFirebaseCall messagesList

            } catch (e: Exception) {
                // Log any errors during Firebase call
                Log.e("FirebaseDebug", "Error fetching messages: ${e.message}")
                return@safeFirebaseCall emptyList<MessageModel>()
            }
        }
    }

}