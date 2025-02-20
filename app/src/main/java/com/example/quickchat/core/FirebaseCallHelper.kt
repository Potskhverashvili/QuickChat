package com.example.quickchat.core

object FirebaseCallHelper {

    suspend fun <T> safeFirebaseCall(
        firebaseCall: suspend () -> T
    ): OperationStatus<T> {
        return try {
            val result = firebaseCall.invoke()
            OperationStatus.Success(result)
        } catch (e: Exception) {
            OperationStatus.Failure(exception = e)
        }
    }
}