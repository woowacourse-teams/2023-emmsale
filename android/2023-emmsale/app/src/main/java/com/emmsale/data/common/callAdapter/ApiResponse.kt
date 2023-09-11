package com.emmsale.data.common.callAdapter

import okhttp3.Headers
import okhttp3.internal.EMPTY_HEADERS

sealed class ApiResponse<out T : Any> {
    fun <R : Any> map(transform: (T) -> R): ApiResponse<R> = when (this) {
        is Success -> Success(transform(data), headers)
        is Failure -> Failure(code, message)
        is NetworkError -> NetworkError
        is Unexpected -> Unexpected(error)
    }
}

data class Success<T : Any>(val data: T, val headers: Headers = EMPTY_HEADERS) : ApiResponse<T>()
data class Failure(val code: Int, val message: String?) : ApiResponse<Nothing>()
object NetworkError : ApiResponse<Nothing>()
data class Unexpected(val error: Throwable?) : ApiResponse<Nothing>()
