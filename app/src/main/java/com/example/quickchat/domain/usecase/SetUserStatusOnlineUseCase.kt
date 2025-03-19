package com.example.quickchat.domain.usecase

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.repository.FirebaseRepository
import javax.inject.Inject

class SetUserStatusOnlineUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(): OperationStatus<Unit> {
        return firebaseRepository.setUserStatusOnline()
    }
}