package com.example.quickchat.domain.usecase

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private var chatRepository: ChatRepository
) {
    suspend fun execute(
        chatId: String,
        senderEmail: String,
        text: String
    ): OperationStatus<Unit> {
        return chatRepository.sendMessage(
            chatId = chatId,
            senderEmail = senderEmail,
            text = text
        )
    }
}