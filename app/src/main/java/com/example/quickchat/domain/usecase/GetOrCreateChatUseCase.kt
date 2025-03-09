package com.example.quickchat.domain.usecase

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.repository.ChatRepository
import javax.inject.Inject

class GetOrCreateChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend fun execute(recipientEmail: String?): OperationStatus<String> {
        return chatRepository.getOrCreateChat(recipientEmail)
    }
}