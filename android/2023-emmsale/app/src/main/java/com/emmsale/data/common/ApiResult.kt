package com.emmsale.data.common

import okhttp3.Headers
import retrofit2.HttpException
import retrofit2.Response

sealed interface ApiResult<T : Any>
class ApiSuccess<T : Any>(val data: T, val header: Headers) : ApiResult<T>
class ApiError<T : Any>(val code: Int, val message: String?) : ApiResult<T>
class ApiException<T : Any>(val e: Throwable) : ApiResult<T>

suspend inline fun <T : Any, reified V : Any> handleApi(
    execute: suspend () -> Response<T>,
    mapToDomain: suspend (T) -> V,
): ApiResult<V> {
    return try {
        val response = execute()
        val body = response.body()
        val headers = response.headers()
        when {
            response.isSuccessful && body == null && V::class == Unit::class -> ApiSuccess(
                Unit as V,
                headers,
            )

            response.isSuccessful && body != null -> ApiSuccess(mapToDomain(body), headers)
            else -> ApiError(code = response.code(), message = response.message())
        }
    } catch (e: HttpException) {
        ApiError(code = e.code(), message = e.message())
    } catch (e: Throwable) {
        ApiException(e)
    }
}
