package com.example.quickchat.presentation.screens.authorization.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.usecase.RegisterNewUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerNewUserUseCase: RegisterNewUserUseCase
) : ViewModel() {

    private val _registerFlow = MutableSharedFlow<String>()
    var registerFlow: SharedFlow<String> = _registerFlow

    private val _showError = MutableSharedFlow<String?>()
    val showError: SharedFlow<String?> = _showError

    private val _isLoadingState = MutableSharedFlow<Boolean>()
    val isLoadingState: SharedFlow<Boolean> = _isLoadingState

    fun registerNewUser(username: String, email: String, password: String) = viewModelScope.launch {
        when (val status = registerNewUserUseCase.execute(username, email, password)) {
            is OperationStatus.Success -> {
                _registerFlow.emit(status.value.toString())
                _isLoadingState.emit(false)
            }

            is OperationStatus.Failure -> {
                _showError.emit(status.exception.message)
                _isLoadingState.emit(false)
            }
        }
    }

    fun areFieldsValid(
        username: String,
        email: String,
        password: String,
        repeatPassword: String
    ): Boolean {
        return when {
            username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() -> {
                viewModelScope.launch { _showError.emit("Please fill in all fields") }
                false
            }

            password != repeatPassword -> {
                viewModelScope.launch { _showError.emit("Passwords do not match") }
                false
            }

            else -> true
        }
    }
}