package com.example.quickchat.data.repository

import com.example.quickchat.core.FirebaseCallHelper
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UserStatus
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : FirebaseRepository {

    override suspend fun registerNewUser(
        username: String, email: String, password: String, status: UserStatus
    ): OperationStatus<FirebaseUser> {
        return FirebaseCallHelper.safeFirebaseCall {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            val userMap = hashMapOf(
                "username" to username,
                "email" to email,
                "status" to UserStatus.ONLINE
            )
            firestore.collection("users").document(user!!.uid).set(userMap).await()
            user
        }
    }

    override suspend fun logInUser(email: String, password: String): OperationStatus<FirebaseUser> {
        return FirebaseCallHelper.safeFirebaseCall {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user!!
            setUserStatusOnline()
            user
        }
    }

    override suspend fun logOutUser(): OperationStatus<Unit> {
        return FirebaseCallHelper.safeFirebaseCall {
            setUserStatusOffline()
            auth.signOut()
        }
    }

    override suspend fun getCurrentUser(): OperationStatus<FirebaseUser> {
        return FirebaseCallHelper.safeFirebaseCall {
            auth.currentUser ?: throw Exception("User is not authenticated")
        }
    }

    override suspend fun getUserProfileInfo(): OperationStatus<UsersModel> {
        return FirebaseCallHelper.safeFirebaseCall {
            val user = auth.currentUser
            val document =
                user?.let { firestore.collection("users").document(it.uid).get().await() }
            val userEmail = user?.email
            val userName = document?.getString("username")
            val userImage = null
            UsersModel(name = userName, userImage = userImage, userEmail = userEmail)
        }
    }

    override suspend fun getOnlineUsers(): OperationStatus<List<UsersModel>> {
        return FirebaseCallHelper.safeFirebaseCall {
            val querySnapshot =
                firestore.collection("users").whereEqualTo("status", UserStatus.ONLINE).get()
                    .await()

            val onlineUsers = querySnapshot.documents.mapNotNull { document ->
                val userName = document.getString("username")
                val userEmail = document.getString("email")
                if (userName != null && userEmail != null) {
                    UsersModel(name = userName, userEmail = userEmail)
                } else {
                    null
                }
            }
            onlineUsers
        }
    }

    override suspend fun getSearchedUsers(query: String): OperationStatus<List<UsersModel>> {
        return FirebaseCallHelper.safeFirebaseCall {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .whereGreaterThanOrEqualTo("username", query.lowercase())
                .whereLessThanOrEqualTo("username", query.lowercase() + "\uf8ff")
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                UsersModel(
                    id = document.id,
                    name = document.getString("username"),
                    userEmail = document.getString("email"),
                    status = UserStatus.fromString(document.getString("status") ?: "OFFLINE")
                )
            }
        }
    }

    override suspend fun getUserById(userId: String): OperationStatus<UsersModel> {
        return FirebaseCallHelper.safeFirebaseCall {
            val document = firestore.collection("users").document(userId).get().await()
            if (!document.exists()) {
                throw Exception("User not found")
            }

            val name = document.getString("username")
            val userImage = document.getLong("userImage")?.toInt()
            val userEmail = document.getString("email")
            val statusString = document.getString("status")

            val status = statusString?.let { UserStatus.valueOf(it) } ?: UserStatus.OFFLINE

            val user = UsersModel(
                id = userId,
                name = name,
                userImage = userImage,
                userEmail = userEmail,
                status = status
            )
            user
        }
    }

    override suspend fun setUserStatusOnline(): OperationStatus<Unit> {
        return FirebaseCallHelper.safeFirebaseCall {
            val user = getCurrentUser()
            if (user is OperationStatus.Success) {
                firestore.runTransaction { transaction ->
                    val docRef = firestore.collection("users").document(user.value.uid)
                    transaction.update(docRef, "status", UserStatus.ONLINE)
                    transaction.update(
                        docRef,
                        "lastUpdated",
                        System.currentTimeMillis()
                    )
                }.await()
            }
        }
    }

    override suspend fun setUserStatusOffline(): OperationStatus<Unit> {
        return FirebaseCallHelper.safeFirebaseCall {
            val user = getCurrentUser()
            if (user is OperationStatus.Success) {
                firestore.runTransaction { transaction ->
                    val docRef = firestore.collection("users").document(user.value.uid)
                    transaction.update(docRef, "status", UserStatus.OFFLINE)
                    transaction.update(
                        docRef,
                        "lastUpdated",
                        System.currentTimeMillis()
                    )
                }.await()
            }
        }
    }
}


