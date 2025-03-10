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
            Log.d("ChatRepository", "Checking if chat exists for chatId: $chatId, exists: ${chatSnapshot.exists()}")

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
                Log.d("ChatRepository", "New chat session created with generalMessages initialized for chatId: $chatId")
            } else {
                // Log that the chat session already exists
                Log.d("ChatRepository", "Chat session already exists for chatId: $chatId")
            }

            // Return the chatId (either new or existing)
            chatId
        }
    }


    override suspend fun sendMessageAndGetAllMessages(
        chatId: String,
        senderEmail: String,
        senderUid: String,
        text: String
    ): OperationStatus<List<MessageModel>> {
        return FirebaseCallHelper.safeFirebaseCall {
            // Initialize FirebaseDatabase with the specific URL
            val firebaseDatabase = FirebaseDatabase
                .getInstance("https://quickchat-d765e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("messages")

            val chatRef = firebaseDatabase.child(chatId)

            // Generate a unique ID for the new message
            val newMessageId = chatRef.child("generalMessages").push().key // Firebase generates a unique key here

            // Prepare the new message to be added
            val newMessage = MessageModel(
                id = newMessageId,  // Set the generated ID
                senderEmail = senderEmail,
                text = text,
                senderUid = senderUid,
                timestamp = System.currentTimeMillis()
            )

            // Log the message being sent
            Log.d("SendMessage", "Sending message: $newMessage")

            // Send the message by adding it to the "generalMessages" list
            newMessageId?.let {
                chatRef.child("generalMessages").child(it).setValue(newMessage).await()
            }

            // Log the data after pushing to Firebase
            Log.d("SendMessage", "Message sent successfully, fetching all messages.")

            // Retrieve all messages
            val messagesSnapshot = chatRef.child("generalMessages").get().await()
            val messages = messagesSnapshot.children.mapNotNull {
                it.getValue(MessageModel::class.java)
            }

            // Log the messages retrieved
            Log.d("SendMessage", "Messages fetched: $messages")

            messages
        }
    }





}