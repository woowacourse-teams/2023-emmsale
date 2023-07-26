package com.emmsale.data.common

import com.emmsale.presentation.KerdyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking(Dispatchers.IO) {
            KerdyApplication.repositoryContainer.tokenRepository.getToken()
        } ?: return chain.proceed(chain.request())

        val newRequest = chain.request().newBuilder()
            .addHeader(ACCESS_TOKEN_HEADER, token.accessToken)
            .build()
        return chain.proceed(newRequest)
    }

    companion object {
        private const val ACCESS_TOKEN_HEADER = "accessToken"
    }
}
