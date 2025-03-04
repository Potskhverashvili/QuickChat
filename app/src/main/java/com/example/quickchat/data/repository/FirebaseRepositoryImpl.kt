package com.example.quickchat.data.repository

import android.util.Log
import com.example.quickchat.core.FirebaseCallHelper
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

class FirebaseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : FirebaseRepository {

    override suspend fun registerNewUser(
        username: String,
        email: String,
        password: String
    ): OperationStatus<FirebaseUser> {
        return FirebaseCallHelper.safeFirebaseCall {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            val userMap = hashMapOf(
                "username" to username,
                "email" to email,
            )
            firestore.collection("users").document(user!!.uid).set(userMap).await()
            user
        }
    }

    override suspend fun logInUser(email: String, password: String): OperationStatus<FirebaseUser> {
        return FirebaseCallHelper.safeFirebaseCall {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user!!
        }
    }

    override suspend fun getUserProfileInfo(): OperationStatus<UsersModel> {
        return FirebaseCallHelper.safeFirebaseCall {
            val user = auth.currentUser
            val document = user?.let { firestore.collection("users").document(it.uid).get().await() }
            val userEmail = user?.email
            val userName = document?.getString("username")
            val userImage = null
            val ragac = UsersModel(name = userName, userImage = userImage, userEmail = userEmail)
            Log.d("MyLog", "$ragac")
            ragac
        }
    }


}


