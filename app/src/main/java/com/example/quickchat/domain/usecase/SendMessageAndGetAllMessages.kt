package com.example.quickchat.domain.usecase

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageAndGetAllMessages @Inject constructor(
    private val chatRepository: ChatRepository
) {
   /* suspend fun execute(
        chatId: String,
        senderEmail: String,
        senderUid: String,
        text: String
    ): OperationStatus<List<MessageModel>> {
        return chatRepository.sendMessageAndGetAllMessages(
            chatId = chatId,
            senderEmail = senderEmail,
            senderUid = senderUid,
            text = text
        )
    }*/
}