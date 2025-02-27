package com.example.quickchat.domain.repository

import com.example.quickchat.core.OperationStatus
import com.google.firebase.auth.FirebaseUser

interface FirebaseRepository {
    suspend fun registerNewUser(
        username: String, email: String, password: String
    ): OperationStatus<FirebaseUser>

    suspend fun logInUser(email: String, password: String) : OperationStatus<FirebaseUser>
}