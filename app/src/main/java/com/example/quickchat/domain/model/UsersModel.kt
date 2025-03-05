package com.example.quickchat.domain.model

import java.util.UUID

data class UsersModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String?,
    val userImage: Int? = null,
    val userEmail: String? = null,
    val status: UserStatus = UserStatus.OFFLINE
)

enum class UserStatus {
    ONLINE,
    OFFLINE
}