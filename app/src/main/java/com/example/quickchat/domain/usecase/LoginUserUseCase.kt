package com.example.quickchat.domain.usecase

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseUser

class LoginUserUseCase(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(email: String, password: String): OperationStatus<FirebaseUser> {
        return firebaseRepository.logInUser(email, password)
    }
}