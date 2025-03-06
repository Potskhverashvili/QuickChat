package com.example.quickchat.domain.usecase

import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetSearchedUsers @Inject constructor(
    private val repository: FirebaseRepository
) {
    suspend fun execute(query: String): OperationStatus<List<UsersModel>> {
        return repository.getSearchedUsers(query = query)
    }
}