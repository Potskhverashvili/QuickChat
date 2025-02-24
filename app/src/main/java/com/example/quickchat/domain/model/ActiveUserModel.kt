package com.example.quickchat.domain.model

import java.util.UUID

data class ActiveUserModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String
)
