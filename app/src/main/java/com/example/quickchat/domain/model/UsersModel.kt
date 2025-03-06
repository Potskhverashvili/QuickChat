package com.example.quickchat.domain.model

import java.util.UUID

data class UsersModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val userImage: Int? = null,
    val userEmail: String? = null,
    var status: UserStatus = UserStatus.OFFLINE
)

enum class UserStatus {
    ONLINE,
    OFFLINE;

    companion object {
        fun fromString(status: String): UserStatus {
            return when (status.uppercase()) {
                "ONLINE" -> ONLINE
                "OFFLINE" -> OFFLINE
                else -> OFFLINE
            }
        }
    }
}

