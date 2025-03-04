package com.example.quickchat.presentation.screens.authorization.ContainerFragment.profile

import java.util.UUID

data class ProfileItem(
    val id: String = UUID.randomUUID().toString(),
    val type: ProfileItemType,
    val userName: String? = null,
    val userEmail: String? = null,
    val userImage: Int? = null,
    val title: String? = null,
    val icon: Int? = null
)

enum class ProfileItemType {
    HEADER, ITEM
}