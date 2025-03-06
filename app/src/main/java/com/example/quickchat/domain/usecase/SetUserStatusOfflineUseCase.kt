package com.example.quickchat.domain.usecase

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.repository.FirebaseRepository
import javax.inject.Inject

class SetUserStatusOfflineUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    suspend fun execute(): OperationStatus<Unit> {
        return repository.setUserStatusOffline()
    }
}