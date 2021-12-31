package cz.davidkurzica.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed class Result<out R> {
    @Serializable
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}