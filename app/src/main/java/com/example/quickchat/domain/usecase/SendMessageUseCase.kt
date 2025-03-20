package com.example.quickchat.domain.usecase

import com.example.quickchat.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend fun execute(
        chatId: String,
        text: String
    ) {
        chatRepository.sendMessage(
            chatId = chatId,
            text = text
        )
    }
}