package com.example.quickchat.domain.repository

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel

interface ChatRepository {

    suspend fun getOrCreateChat(recipientEmail: String?): OperationStatus<String>

    suspend fun createOrGetChatSession(
        currentUserUid: String,
        otherUserUid: String
    ): OperationStatus<String>

    suspend fun sendMessage(
        chatId: String,
        senderEmail: String,
        senderUid: String,
        text: String
    )

    suspend fun listenForMessages(
        chatId: String,
        onMessageReceived: (MessageModel) -> Unit
    )

}