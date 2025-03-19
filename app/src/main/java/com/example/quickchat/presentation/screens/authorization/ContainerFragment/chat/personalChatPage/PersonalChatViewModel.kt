package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.personalChatPage

import android.util.Log.d
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.usecase.CreateOrGetChatSession
import com.example.quickchat.domain.usecase.GetUserByIdUseCase
import com.example.quickchat.domain.usecase.ListenerForMessagesUseCase
import com.example.quickchat.domain.usecase.SendMessage
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val sendMessage: SendMessage,
    private val listenerForMessagesUseCase: ListenerForMessagesUseCase
) : ViewModel() {

    private val _chatId = MutableStateFlow<String?>(null)
    val chatId: StateFlow<String?> = _chatId.asStateFlow()

    private val _messages = MutableSharedFlow<List<MessageModel>>()
    val messages = _messages.asSharedFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private val currentMessages = mutableListOf<MessageModel>()

    private var _userToMessageName = MutableSharedFlow<UsersModel>()
    val userToMessageName = _userToMessageName.asSharedFlow()

    fun getCurrentUserToMessage(id: String) = viewModelScope.launch {
        when (val status = getUserByIdUseCase.execute(userId = id)) {
            is OperationStatus.Success -> {
                if (status.value.name.isNullOrEmpty()) {
                    Log.e("PersonalChatViewModel", "User name is null or empty!")
                }
                _userToMessageName.emit(status.value)
            }
            is OperationStatus.Failure -> {
                Log.e("PersonalChatViewModel", "Failed to get user: ${status.exception.message}")
            }
            is OperationStatus.Loading -> {
                Log.d("PersonalChatViewModel", "Loading user data...")
            }
        }
    }

    fun createOrGetChatSession(currentUserUid: String, otherUserUid: String) =
        viewModelScope.launch {
            when (val status = createOrGetChatSession.execute(currentUserUid, otherUserUid)) {
                is OperationStatus.Success -> {
                    _chatId.emit(status.value)
                    listenForMessages(status.value)
                }

                is OperationStatus.Failure -> {}
                is OperationStatus.Loading -> {}
            }
        }

    fun sendMessage(
        chatId: String,
        senderEmail: String,
        senderUid: String,
        messageText: String
    ) = viewModelScope.launch {
        _loadingState.emit(true)
        sendMessage.execute(chatId, senderEmail, senderUid, messageText)
        _loadingState.emit(false)
    }

    private fun listenForMessages(chatId: String) = viewModelScope.launch {
        currentMessages.clear()
        listenerForMessagesUseCase.execute(chatId)
            .collect { newMessage ->
                currentMessages.add(newMessage)
                _messages.emit(currentMessages.toList()) // Emit updated message list
            }
    }

    override fun onCleared() {
        d("debaugChat", "onCleared")
        super.onCleared()
    }

}