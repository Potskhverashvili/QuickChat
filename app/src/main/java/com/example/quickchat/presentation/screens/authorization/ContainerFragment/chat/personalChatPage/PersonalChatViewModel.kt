package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.personalChatPage

import android.util.Log.d
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.MessageModel
import com.example.quickchat.domain.usecase.CreateOrGetChatSession
import com.example.quickchat.domain.usecase.GetUserByIdUseCase
import com.example.quickchat.domain.usecase.SendMessageAndGetAllMessages
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
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


    private val firebaseDatabase = FirebaseDatabase
        .getInstance("https://chatapp-d7d86-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("messages").child("cdvP7SZYNLMZAGjouGXCi813X4g2_njOjVmtvi6UOh2drPzDkyCNVhHd2")


    init {
        firebaseDatabase.addChildEventListener(object : ChildEventListener {
            val messagesList = mutableListOf<MessageModel>()
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(MessageModel::class.java)
                if (message != null) {
                    messagesList.add(message)
                    d("checkMessages" , message.text.toString())
                }

                viewModelScope.launch {
                    _messages.emit(messagesList.reversed())
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) = Unit
            override fun onChildRemoved(snapshot: DataSnapshot) = Unit
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) = Unit
            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

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


}