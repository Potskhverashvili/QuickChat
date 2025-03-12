package com.example.quickchat.domain.usecase

import com.example.quickchat.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessage @Inject constructor(
    private val chatRepository: ChatRepository
) {
   /* suspend fun execute(
        chatId: String,
        senderEmail: String,
        senderUid: String,
        text: String
    ) {
        chatRepository.sendMessage(
            chatId = chatId,
            senderEmail = senderEmail,
            senderUid = senderUid,
            text = text
        )
    }*/
}