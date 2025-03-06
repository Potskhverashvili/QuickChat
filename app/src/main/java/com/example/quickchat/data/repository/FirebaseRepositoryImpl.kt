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
    private val database: FirebaseDatabase
) : FirebaseRepository {

    override suspend fun registerNewUser(
        username: String,
        email: String,
        password: String,
        status: String
    ): OperationStatus<FirebaseUser> {
        return FirebaseCallHelper.safeFirebaseCall {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            val userMap = hashMapOf(
                "username" to username,
                "email" to email,
                "status" to status
            )
            firestore.collection("users").document(user!!.uid).set(userMap).await()
            user
        }
    }

    override suspend fun logInUser(email: String, password: String): OperationStatus<FirebaseUser> {
        return FirebaseCallHelper.safeFirebaseCall {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user!!
            user
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

   /* override suspend fun getOnlineUsers(): OperationStatus<List<UsersModel>> {
        return FirebaseCallHelper.safeFirebaseCall {
            val db = FirebaseFirestore.getInstance()

            val usersSnapshot = db.collection("users")
                .whereEqualTo("status", UserStatus.OFFLINE.name) // Filter by online status
                .get()
                .await() // Suspend until Firestore returns the result

            usersSnapshot.documents.mapNotNull { document ->

                document.toObject(UsersModel::class.java)
            }

            *//*val user = doc.toObject(User::class.java)
            user?.copy(uid = doc.id)*//*

        }
    }*/

    override suspend fun getOnlineUsers(): OperationStatus<List<UsersModel>> {
        return FirebaseCallHelper.safeFirebaseCall {
            // Query Firestore for users whose status is "online"
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("status", "online")
                .get()
                .await()

            // Map the query snapshot to a list of UsersModel
            val onlineUsers = querySnapshot.documents.mapNotNull { document ->
                val userName = document.getString("username")
                val userEmail = document.getString("email")
                // Assuming the user image is in the document

                if (userName != null && userEmail != null) {
                    UsersModel(name = userName, userEmail = userEmail)
                } else {
                    null
                }
            }

            onlineUsers
        }
    }

    override suspend fun userWentOffline(): OperationStatus<UsersModel> {
        return FirebaseCallHelper.safeFirebaseCall {
            val user = auth.currentUser
            // Get the user document by UID from Firestore
            val userDocument =
                user?.let { firestore.collection("users").document(user.uid).get().await() }
                // Update the status to "offline"
                firestore.collection("users").document(user!!.uid).update("status", "offline").await()

                // Fetch the updated user information after updating the status
                val updatedUser = userDocument?.toObject(UsersModel::class.java)

                // Return the updated user model
                updatedUser ?: throw Exception("User data is null after update")
        }
    }
}


