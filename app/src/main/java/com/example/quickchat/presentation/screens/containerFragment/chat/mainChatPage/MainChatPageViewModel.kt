package com.example.quickchat.presentation.screens.containerFragment.chat.mainChatPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.usecase.GetOnlineUsersUseCase
import com.example.quickchat.domain.usecase.SetUserStatusOfflineUseCase
import com.example.quickchat.domain.usecase.SetUserStatusOnlineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainChatPageViewModel @Inject constructor(
    private val getOnlineUsersUseCase: GetOnlineUsersUseCase,
) : ViewModel() {
    private val _onlineUsers = MutableStateFlow<List<UsersModel>>(emptyList())
    val onlineUsers = _onlineUsers.asStateFlow()

    fun fetchOnlineUsers() = viewModelScope.launch {
        when (val result = getOnlineUsersUseCase.execute()) {
            is OperationStatus.Success -> {
                _onlineUsers.value = result.value
            }
            is OperationStatus.Failure -> {}
        }
    }
}