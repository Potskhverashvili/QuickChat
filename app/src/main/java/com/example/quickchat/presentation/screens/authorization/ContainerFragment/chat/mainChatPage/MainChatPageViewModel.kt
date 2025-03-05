package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.mainChatPage

import android.util.Log.d
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.domain.usecase.GetOnlineUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainChatPageViewModel @Inject constructor(
    private val onlineUsersUseCase: GetOnlineUsersUseCase
) : ViewModel() {
    fun getOnlineUsers() = viewModelScope.launch {
        d("onlineUsers", "${onlineUsersUseCase.execute()}")
    }
}