package com.example.quickchat.presentation.screens.authorization.ContainerFragment.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.R
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.usecase.GetUserProfileUseCase
import com.example.quickchat.domain.usecase.LogOutUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logOutUserUseCase: LogOutUserUseCase
) : ViewModel() {

    private val _profileItems = MutableStateFlow<List<ProfileItem>>(emptyList())
    val profileItems = _profileItems.asStateFlow()

    private val user = MutableStateFlow<UsersModel?>(null)

    init {
        getUserProfileUseCase()
        loadProfileItems()
    }


    fun logOutUser() = viewModelScope.launch{
        when(val status = logOutUserUseCase.execute()){
            is OperationStatus.Success -> {

            }

            is OperationStatus.Failure -> {

            }

            is OperationStatus.Loading -> {

            }
        }
    }

    private fun getUserProfileUseCase() = viewModelScope.launch {
        when (val status = getUserProfileUseCase.execute()) {
            is OperationStatus.Success -> {
                Log.d("MyLog", "$status")
                user.emit(status.value)
                loadProfileItems()
            }

            is OperationStatus.Failure -> {

            }

            is OperationStatus.Loading -> {

            }
        }
    }

    private fun loadProfileItems() {
        _profileItems.value = listOf(
            ProfileItem(
                type = ProfileItemType.HEADER,
                userName = user.value?.name,
                userEmail = user.value?.userEmail,
                userImage = R.drawable.ic_profile_image_default
            ),
            ProfileItem(
                type = ProfileItemType.ITEM,
                title = "Appearance",
                icon = R.drawable.ic_appearence
            ),
            ProfileItem(
                type = ProfileItemType.ITEM,
                title = "Notification",
                icon = R.drawable.ic_notification
            )
        )
    }

}