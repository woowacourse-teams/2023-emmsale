package com.emmsale.data.common

import com.emmsale.presentation.KerdyApplication
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { KerdyApplication.repositoryContainer.tokenRepository.getToken() }
        val newRequest = chain.request().newBuilder()
            .addHeader(ACCESS_TOKEN_HEADER, token?.accessToken ?: INVALID_ACCESS_TOKEN)
            .build()
        return chain.proceed(newRequest)
    }

    companion object {
        private const val ACCESS_TOKEN_HEADER = "accessToken"
        private const val INVALID_ACCESS_TOKEN = "invalidAccessToken"
    }
}
