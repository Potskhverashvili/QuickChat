package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.mainChatPage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.usecase.GetOnlineUsersUseCase
import com.example.quickchat.domain.usecase.UserWentOfflineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainChatPageViewModel @Inject constructor(
    private val getOnlineUsersUseCase: GetOnlineUsersUseCase,
    private val userWentOfflineUseCase: UserWentOfflineUseCase
) : ViewModel() {

    private val _onlineUsers = MutableStateFlow<List<UsersModel>>(emptyList())
    val onlineUsers = _onlineUsers.asStateFlow()

    fun fetchOnlineUsers() = viewModelScope.launch {
        when (val result = getOnlineUsersUseCase.execute()) {
            is OperationStatus.Success -> {
                Log.d("HEREMYLOG", "result => ${result.value}")
                // Update the StateFlow with the list of online users
                _onlineUsers.value = result.value
            }

            is OperationStatus.Failure -> {
                // Handle failure if necessary (e.g., show error message)
            }

            is OperationStatus.Loading -> {
                // Optionally handle loading state
            }
        }
    }

    fun userWentOffline() = viewModelScope.launch {
        when (val result = userWentOfflineUseCase.execute()) {
            is OperationStatus.Success -> {
                Log.d("HEREMYLOG", "result => ${result.value}")
                fetchOnlineUsers()
            }

            is OperationStatus.Failure -> {
                // Handle failure if necessary (e.g., show error message)
            }

            is OperationStatus.Loading -> {
                // Optionally handle loading state
            }
        }

    }

}