package com.example.quickchat.domain.usecase

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.repository.ChatRepository
import javax.inject.Inject

class RetrieveAllMessages @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend fun execute(chatId: String): OperationStatus<List<MessageModel>> {
        return chatRepository.getMessages(chatId = chatId)
    }
}