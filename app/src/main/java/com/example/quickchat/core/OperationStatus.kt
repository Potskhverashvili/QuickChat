package com.example.quickchat.core

sealed interface OperationStatus<T> {
    data class Success<T>(val value: T) : OperationStatus<T>
    data class Failure<T>(val exception: Exception) : OperationStatus<T>
}

fun <FromType, ToType> OperationStatus<FromType>.map(
    mapper: (FromType) -> ToType
): OperationStatus<ToType> {
    return when (this) {
        is OperationStatus.Success -> {
            OperationStatus.Success(mapper(value))
        }
        is OperationStatus.Failure -> {
            OperationStatus.Failure(exception)
        }
    }
}