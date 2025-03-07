package com.example.quickchat.domain.usecase

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(userId: String): OperationStatus<UsersModel> {
        return firebaseRepository.getUserById(userId)
    }
}