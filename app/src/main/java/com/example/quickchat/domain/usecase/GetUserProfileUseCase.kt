package com.example.quickchat.domain.usecase

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    suspend fun execute(): OperationStatus<UsersModel> {
        return repository.getUserProfileInfo()
    }
}