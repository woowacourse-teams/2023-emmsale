package com.emmsale.data.network.callAdapter

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class KerdyCallAdapterFactory : Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) return null // Call
        check(returnType is ParameterizedType) { NOT_GENERIC_TYPE.format(returnType) }

        val responseType = getParameterUpperBound(0, returnType) // ApiResponse
        if (getRawType(responseType) != ApiResponse::class.java) return null
        check(responseType is ParameterizedType) { NOT_GENERIC_TYPE.format(responseType) }

        val genericType = getParameterUpperBound(0, responseType) // DTO
        return KerdyCallAdapter<Any>(genericType)
    }

    companion object {
        private const val NOT_GENERIC_TYPE = "[ERROR] Generic 타입이어야 합니다."
    }
}
