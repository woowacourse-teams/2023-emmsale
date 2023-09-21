package com.emmsale.data.common.callAdapter

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

class KerdyCall<T : Any>(
    private val call: Call<T>,
    private val responseType: Type,
) : Call<ApiResponse<T>> {

    @Suppress("UNCHECKED_CAST")
    override fun enqueue(callback: Callback<ApiResponse<T>>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, result: Response<T>) {
                val apiResult = when {
                    !result.isSuccessful -> Failure(result.code(), result.errorBody()?.string())
                    responseType == Unit::class.java -> Success(Unit as T, result.headers())
                    result.body() == null -> Unexpected(IllegalStateException(NOT_EXIST_BODY))
                    else -> Success(result.body()!!, result.headers())
                }
                callback.onResponse(this@KerdyCall, Response.success(apiResult))
            }

            override fun onFailure(call: Call<T>, error: Throwable) {
                val apiResult = when (error) {
                    is IOException -> NetworkError
                    else -> Unexpected(error)
                }
                callback.onResponse(this@KerdyCall, Response.success(apiResult))
            }
        })
    }

    override fun clone(): Call<ApiResponse<T>> = KerdyCall<T>(call.clone(), responseType)

    override fun execute(): Response<ApiResponse<T>> {
        throw UnsupportedOperationException(NOT_SUPPORT_EXECUTE)
    }

    override fun isExecuted(): Boolean = call.isExecuted

    override fun cancel() = call.cancel()

    override fun isCanceled(): Boolean = call.isCanceled

    override fun request(): Request = call.request()

    override fun timeout(): Timeout = call.timeout()

    companion object {
        private const val NOT_SUPPORT_EXECUTE = "[ERROR] Kerdy는 execute를 지원하지 않습니다."
        private const val NOT_EXIST_BODY = "[ERROR] Response body가 존재하지 않습니다."
    }
}
