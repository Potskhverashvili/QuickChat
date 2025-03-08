package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.personalChatPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.usecase.CreateOrGetChatSession
import com.example.quickchat.domain.usecase.GetUserByIdUseCase
import com.example.quickchat.domain.usecase.RetrieveAllMessages
import com.example.quickchat.domain.usecase.SendMessageUseCase
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
    private val sendMessageUseCase: SendMessageUseCase,
    private val retrieveAllMessages: RetrieveAllMessages
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
                }

                is OperationStatus.Failure -> {}
                is OperationStatus.Loading -> {}
            }
        }

    fun startMessaging(chatId: String, senderEmail: String, text: String) {
        viewModelScope.launch {
            // Send the message using the repository
            when (val status = sendMessageUseCase.execute(chatId, senderEmail, text)) {
                is OperationStatus.Success -> {
                    getMessages(chatId)
                }

                is OperationStatus.Failure -> {
                    // Handle failure
                }

                is OperationStatus.Loading -> {
                    // Handle loading state
                }
            }
        }
    }


    fun getMessages(chatId: String) = viewModelScope.launch {
        _loadingState.emit(true) // Set loading state to true
        when (val status = retrieveAllMessages.execute(chatId)) {
            is OperationStatus.Success -> {
                _messages.emit(status.value)
            }

            is OperationStatus.Failure -> {
                // Handle failure
            }

            is OperationStatus.Loading -> {
                // Handle loading state
            }
        }
        _loadingState.emit(false) // Set loading state to true
    }
}