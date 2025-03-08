package com.example.quickchat.domain.repository

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel

interface ChatRepository {

    suspend fun createOrGetChatSession(
        currentUserUid: String,
        otherUserUid: String
    ): OperationStatus<String>

    suspend fun sendMessage(
        chatId: String,
        senderEmail: String,
        text: String
    ): OperationStatus<Unit>

    suspend fun getMessages(chatId: String): OperationStatus<List<MessageModel>>

}