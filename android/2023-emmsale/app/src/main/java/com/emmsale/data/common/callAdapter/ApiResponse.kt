package com.emmsale.data.common.callAdapter

sealed class ApiResponse<out T : Any> {
    fun <R : Any> map(transform: (T) -> R): ApiResponse<R> = when (this) {
        is Success -> Success(transform(data))
        is Failure -> Failure(responseCode, message)
        is NetworkError -> NetworkError
        is Unexpected -> Unexpected(error)
    }
}

data class Success<T : Any>(val data: T) : ApiResponse<T>()
data class Failure(val responseCode: Int, val message: String?) : ApiResponse<Nothing>()
object NetworkError : ApiResponse<Nothing>()
data class Unexpected(val error: Throwable?) : ApiResponse<Nothing>()
