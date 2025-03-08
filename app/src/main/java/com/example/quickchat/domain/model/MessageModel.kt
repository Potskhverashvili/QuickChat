package com.example.quickchat.domain.model

data class MessageModel(
    val id: String? = null,
    val text: String? = null,
    val senderUid: String? = null,
    val senderEmail: String? = null,
    val timestamp: Long? = null
)
