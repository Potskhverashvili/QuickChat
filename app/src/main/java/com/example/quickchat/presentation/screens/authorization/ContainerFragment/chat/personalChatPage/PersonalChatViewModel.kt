package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.personalChatPage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.usecase.CreateOrGetChatSession
import com.example.quickchat.domain.usecase.GetUserByIdUseCase
import com.example.quickchat.domain.usecase.ListenerForMessagesUseCase
import com.example.quickchat.domain.usecase.SendMessage
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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

    var id: String = chatId.toString()

    private val _messages = MutableSharedFlow<List<MessageModel>>(replay = 1)
    val messages = _messages.asSharedFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private val currentMessages = mutableListOf<MessageModel>()

    fun createOrGetChatSession(currentUserUid: String, otherUserUid: String) =
        viewModelScope.launch {
            when (val status = createOrGetChatSession.execute(currentUserUid, otherUserUid)) {
                is OperationStatus.Success -> {
                    _chatId.emit(status.value)
                    listenForMessages(status.value) // Set up listener when chat ID is available
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
        listenerForMessagesUseCase.execute(chatId = chatId) { newMessage ->
            viewModelScope.launch {
                currentMessages.add(newMessage)
                _messages.emit(currentMessages.toList()) // Emit updated message list
            }
        }
    }

}

//    private fun listenForMessages(chatId: String) = viewModelScope.launch {
//        Log.d("PersonalChatViewModel", "Calling listenerForMessagesUseCase.execute with chatId: $chatId")
//        // Execute the use case
//        when (val status = listenerForMessagesUseCase.execute(chatId)) {
//            is OperationStatus.Success -> {
//                val messagesList = status.value
//                _messages.emit(messagesList)
//                Log.d("PersonalChatViewModel", "Messages fetched: $messagesList")
//            }
//            is OperationStatus.Failure -> {
//                Log.d("PersonalChatViewModel", "Failed to fetch messages: ${status.exception}")
//            }
//            is OperationStatus.Loading -> {
//                Log.d("PersonalChatViewModel", "Loading messages...")
//            }
//        }
//    }




