package com.example.quickchat.presentation.screens.authorization.login

import android.util.Log.d
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.usecase.LoginUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _loginFlow = MutableSharedFlow<String?>()
    val loginFlow: SharedFlow<String?> = _loginFlow

    private val _showError = MutableSharedFlow<String?>()
    val showError: SharedFlow<String?> = _showError

    private val _isLoadingState = MutableSharedFlow<Boolean>()
    val isLoadingState: SharedFlow<Boolean> = _isLoadingState

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        when (val result = loginUserUseCase.execute(email, password)) {
            is OperationStatus.Success -> {
                _loginFlow.emit(result.value.email)
            }

            is OperationStatus.Failure -> {
                _showError.emit(result.exception.message)
            }

            is OperationStatus.Loading -> {
                _isLoadingState.emit(true)
            }
        }
    }


    fun areFieldsValid(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> {
                viewModelScope.launch { _showError.emit("Please fill in all fields") }
                false
            }
            else -> true
        }
    }
}