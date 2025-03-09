package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.personalChatPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.usecase.CreateOrGetChatSession
import com.example.quickchat.domain.usecase.GetUserByIdUseCase
import com.example.quickchat.domain.usecase.SendMessageAndGetAllMessages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalChatViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val createOrGetChatSession: CreateOrGetChatSession,
    private val sendMessageAndGetAllMessages: SendMessageAndGetAllMessages
) : ViewModel() {

    private val _chatId = MutableStateFlow<String?>(null)
    val chatId: StateFlow<String?> = _chatId.asStateFlow()

    private val _messages = MutableStateFlow<List<MessageModel>>(emptyList())
    val messages: StateFlow<List<MessageModel>> = _messages.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    fun createOrGetChatSession(currentUserUid: String, otherUserUid: String) =
        viewModelScope.launch {
            when (val status = createOrGetChatSession.execute(currentUserUid, otherUserUid)) {
                is OperationStatus.Success -> {
                    _chatId.emit(status.value)
                    fetchMessages(status.value)  // Fetch messages immediately
                }

                is OperationStatus.Failure -> {}
                is OperationStatus.Loading -> {}
            }
        }

    fun sendMessageAndGetAllMessages(
        chatId: String,
        senderEmail: String,
        senderUid: String,
        messageText: String
    ) = viewModelScope.launch {
        _loadingState.emit(true)

        when (val status =
            sendMessageAndGetAllMessages.execute(chatId, senderEmail, senderUid, messageText)) {
            is OperationStatus.Success -> {
                _messages.value += status.value // Append instead of replacing
            }

            is OperationStatus.Failure -> {
                // Handle error (e.g., show a Toast)
            }

            is OperationStatus.Loading -> {}
        }

        _loadingState.emit(false)
    }

    private fun fetchMessages(chatId: String) =
        viewModelScope.launch {
            _loadingState.emit(true)
            when (val status = sendMessageAndGetAllMessages.execute(chatId, "", "", "")) {
                is OperationStatus.Success -> {
                    _messages.value = status.value // Ensure we update properly
                }

                is OperationStatus.Failure -> {}
                is OperationStatus.Loading -> {}
            }
            _loadingState.emit(false)
        }

}