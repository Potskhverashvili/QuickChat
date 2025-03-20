package com.example.quickchat.presentation.screens.containerFragment.chat.personalChatPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.usecase.CreateOrGetChatSession
import com.example.quickchat.domain.usecase.GetUserByIdUseCase
import com.example.quickchat.domain.usecase.ListenerForMessagesUseCase
import com.example.quickchat.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalChatViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val createOrGetChatSession: CreateOrGetChatSession,
    private val sendMessageUseCase: SendMessageUseCase,
    private val listenerForMessagesUseCase: ListenerForMessagesUseCase
) : ViewModel() {

    private val _chatId = MutableStateFlow<String?>(null)
    val chatId: StateFlow<String?> = _chatId.asStateFlow()

    private val _messages = MutableSharedFlow<List<MessageModel>>()
    val messages = _messages.asSharedFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private val currentMessages = mutableListOf<MessageModel>()

    private var _receiverName = MutableSharedFlow<UsersModel>()
    val receiverName = _receiverName.asSharedFlow()

    private fun getReceiverUser(id: String) = viewModelScope.launch {
        when (val status = getUserByIdUseCase.execute(userId = id)) {
            is OperationStatus.Success -> {
                _receiverName.emit(status.value)
            }
            is OperationStatus.Failure -> {}
        }
    }

    fun createOrGetChatSession(receiverUid: String) =
        viewModelScope.launch {
            _loadingState.emit(true)
            delay(500)
            when (val status = createOrGetChatSession.execute(receiverUid)) {
                is OperationStatus.Success -> {
                    getReceiverUser(receiverUid)
                    _chatId.emit(status.value)
                    listenForMessages(status.value)
                }

                is OperationStatus.Failure -> {}
            }
            _loadingState.emit(false)
        }

    fun sendMessage(
        chatId: String,
        messageText: String
    ) = viewModelScope.launch {
        _loadingState.emit(true)
        sendMessageUseCase.execute(chatId, messageText)
        _loadingState.emit(false)
    }

    private fun listenForMessages(chatId: String) = viewModelScope.launch {
        currentMessages.clear()
        listenerForMessagesUseCase.execute(chatId)
            .collect { newMessage ->
                currentMessages.add(newMessage)
                _messages.emit(currentMessages.toList())
            }
    }
}