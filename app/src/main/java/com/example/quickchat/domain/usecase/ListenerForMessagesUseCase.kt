package com.example.quickchat.domain.usecase

import android.util.Log.d
import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.repository.ChatRepository
import javax.inject.Inject

class ListenerForMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend fun execute(chatId: String, onMessageReceived: (MessageModel) -> Unit) {
        d("listenerCheck", "UseCase execute")
        chatRepository.listenForMessages(chatId, onMessageReceived)
    }
}