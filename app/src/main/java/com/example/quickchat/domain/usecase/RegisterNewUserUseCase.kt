package com.example.quickchat.domain.usecase

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class RegisterNewUserUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    suspend fun execute(
        username: String, email: String, password: String
    ): OperationStatus<FirebaseUser> {
        return firebaseRepository.registerNewUser(username, email, password)
    }

}