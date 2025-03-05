package com.example.quickchat.domain.usecase

import android.util.Log
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetOnlineUsersUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    suspend fun execute(): OperationStatus<List<UsersModel>> {
        Log.d("HEREMYLOG", "GetOnlineUsersUseCase.execute() started")
        return repository.getOnlineUsers()
    }
}