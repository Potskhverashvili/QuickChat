package com.example.quickchat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.usecase.GetCurrentUserUseCase
import com.example.quickchat.domain.usecase.SetUserStatusOfflineUseCase
import com.example.quickchat.domain.usecase.SetUserStatusOnlineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val setUserStatusOnlineUseCase: SetUserStatusOnlineUseCase,
    private val setUserStatusOfflineUseCase: SetUserStatusOfflineUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    //TODO handle Success and Failure
    fun setUserStatusOnline() = viewModelScope.launch {
        when (val status = setUserStatusOnlineUseCase.execute()) {
            is OperationStatus.Success -> {}
            is OperationStatus.Failure -> {}
        }
    }

    fun setUserStatusOffline() = viewModelScope.launch {
        when (val status = setUserStatusOfflineUseCase.execute()) {
            is OperationStatus.Success -> {}
            is OperationStatus.Failure -> {}
        }
    }

    fun getCurrentUser(): Boolean {
        var result = false
        viewModelScope.launch {
            when (val status = getCurrentUserUseCase.execute()) {
                is OperationStatus.Success -> {
                    result = true
                }
                is OperationStatus.Failure -> {}
            }
        }
        return result
    }
}