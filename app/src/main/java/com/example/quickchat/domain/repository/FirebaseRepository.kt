package com.example.quickchat.domain.repository

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UserStatus
import com.example.quickchat.domain.model.UsersModel
import com.google.firebase.auth.FirebaseUser

interface FirebaseRepository {
    suspend fun registerNewUser(
        username: String, email: String, password: String, status: UserStatus = UserStatus.ONLINE
    ): OperationStatus<FirebaseUser>

    suspend fun logInUser(email: String, password: String): OperationStatus<FirebaseUser>

    suspend fun logOutUser(): OperationStatus<Unit>

    suspend fun getCurrentUser(): OperationStatus<FirebaseUser>

    suspend fun getUserProfileInfo(): OperationStatus<UsersModel>

    suspend fun getOnlineUsers(): OperationStatus<List<UsersModel>>

    suspend fun setUserStatusOffline(): OperationStatus<Unit>

    suspend fun setUserStatusOnline(): OperationStatus<Unit>

    suspend fun getSearchedUsers(query: String): OperationStatus<List<UsersModel>>

}