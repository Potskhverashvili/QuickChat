package com.example.quickchat.domain.model

import java.util.UUID

data class OnlineUserModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String
)
