package com.emmsale.data.common.callAdapter

sealed class ApiResponse<out T : Any>
data class Success<T : Any>(val data: T) : ApiResponse<T>()
data class Failure(val responseCode: Int, val message: String?) : ApiResponse<Nothing>()
object NetworkError : ApiResponse<Nothing>()
data class Unexpected(val error: Throwable?) : ApiResponse<Nothing>()
