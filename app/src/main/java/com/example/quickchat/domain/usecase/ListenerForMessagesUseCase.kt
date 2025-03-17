package com.example.quickchat.domain.usecase

import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListenerForMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend fun execute(chatId: String): Flow<MessageModel> {
        return chatRepository.listenForMessages(chatId)
    }
}