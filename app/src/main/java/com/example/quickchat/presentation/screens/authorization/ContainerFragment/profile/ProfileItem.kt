package com.example.quickchat.presentation.screens.authorization.ContainerFragment.profile

import com.example.quickchat.R
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

//sealed class ProfilePageModel {
//    data class Header(
//        val id: String = UUID.randomUUID().toString(),
//        val userName: String?,
//        val userEmail: String?,
//        val userImage: Int?
//    ) : ProfilePageModel()
//
//    data class Item(
//        val id: String = UUID.randomUUID().toString(),
//        val title: String?,
//        val icon: Int?
//    ) : ProfilePageModel()
//}
//
//enum class ProfileItemType {
//    HEADER, ITEM
//}
//
//val profileItems = listOf(
//    ProfilePageModel.Header(
//        id = UUID.randomUUID().toString(),  // Unique ID for header
//        userName = "User Name",
//        userEmail = "user.name@gmail.com",
//        userImage = R.drawable.ic_profile_image_default
//    ),
//    ProfilePageModel.Item(
//        id = UUID.randomUUID().toString(),  // Unique ID for item
//        title = "Appearance",
//        icon = R.drawable.ic_appearence
//    ),
//    ProfilePageModel.Item(
//        id = UUID.randomUUID().toString(),  // Unique ID for item
//        title = "Notification",
//        icon = R.drawable.ic_notification
//    )
//)