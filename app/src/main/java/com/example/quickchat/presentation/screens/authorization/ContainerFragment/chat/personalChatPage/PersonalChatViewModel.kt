package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.personalChatPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.usecase.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalChatViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {


    private val _userById = MutableStateFlow<UsersModel?>(null)
    val userById = _userById.asStateFlow()

    fun getUserById(userId: String) = viewModelScope.launch {

        when (val status = getUserByIdUseCase.execute(userId)) {
            is OperationStatus.Success -> {
                _userById.emit(status.value)
            }

            is OperationStatus.Failure -> {

            }

            is OperationStatus.Loading -> {

            }
        }
    }
}