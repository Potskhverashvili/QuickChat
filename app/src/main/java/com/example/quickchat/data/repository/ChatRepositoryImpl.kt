package com.example.quickchat.data.repository

import com.example.quickchat.core.FirebaseCallHelper
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var databaseReference: DatabaseReference
) : ChatRepository {
    private var chatRef: DatabaseReference? = null

    override suspend fun createOrGetChatSession(
        receiverUid: String
    ): OperationStatus<String> {
        return FirebaseCallHelper.safeFirebaseCall {
            val currentUserUid = auth.currentUser?.uid ?: ""
            val chatId = listOf(currentUserUid, receiverUid).sorted().joinToString("_")
            val chatRef = databaseReference.child(chatId)
            val chatSnapshot = chatRef.get().await()
            if (!chatSnapshot.exists()) {
                val chatData = mapOf(
                    "chatId" to chatId,
                    "users" to listOf(currentUserUid, receiverUid),
                    "createdAt" to System.currentTimeMillis(),
                    "generalMessages" to mutableListOf<MessageModel>()
                )
                chatRef.setValue(chatData).await()
            }
            chatId
        }
    }

    override suspend fun sendMessage(
        chatId: String,
        text: String
    ) {
        FirebaseCallHelper.safeFirebaseCall {
            val chatRef = databaseReference.child(chatId)
            val newMessageId = chatRef.child("generalMessages").push().key
            val senderEmail = auth.currentUser?.email
            val senderUid = auth.uid
            val newMessage = MessageModel(
                id = newMessageId,
                senderEmail = senderEmail,
                text = text,
                senderUid = senderUid,
                timestamp = System.currentTimeMillis()
            )
            newMessageId?.let {
                chatRef.child("generalMessages").child(it).setValue(newMessage).await()
            }
            chatRef.child("generalMessages").get().await()
        }
    }

    override suspend fun listenForMessages(chatId: String): Flow<MessageModel> = callbackFlow {
        chatRef = databaseReference
            .child(chatId)
            .child("generalMessages")

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(MessageModel::class.java)?.let { message ->
                    trySend(message).isSuccess
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) = Unit
            override fun onChildRemoved(snapshot: DataSnapshot) = Unit
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) = Unit
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        chatRef?.addChildEventListener(childEventListener)
        awaitClose { chatRef?.removeEventListener(childEventListener) }
    }
}