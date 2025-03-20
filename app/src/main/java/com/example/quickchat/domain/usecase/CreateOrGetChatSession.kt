package com.example.quickchat.domain.usecase

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.repository.ChatRepository
import javax.inject.Inject

class CreateOrGetChatSession @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend fun execute(
        receiverUid: String
    ): OperationStatus<String> {
        return chatRepository.createOrGetChatSession(receiverUid)
    }
}