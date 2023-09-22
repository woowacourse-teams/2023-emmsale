package com.emmsale.data.common

import android.content.Context
import com.emmsale.data.repository.interfaces.TokenRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val tokenRepository = EntryPointAccessors
        .fromApplication<AuthInterceptorEntryPoint>(context)
        .getTokenRepository()

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenRepository.getToken()
        val newRequest = chain.request().newBuilder()
            .addHeader(ACCESS_TOKEN_HEADER, ACCESS_TOKEN_FORMAT.format(token?.accessToken))
            .build()
        return chain.proceed(newRequest)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AuthInterceptorEntryPoint {
        fun getTokenRepository(): TokenRepository
    }

    companion object {
        private const val ACCESS_TOKEN_HEADER = "authorization"
        private const val ACCESS_TOKEN_FORMAT = "Bearer %s"
    }
}
