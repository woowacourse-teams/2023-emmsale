package com.emmsale.data.common.retrofit

import android.content.Context
import android.content.Intent
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.ui.login.LoginActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    private val tokenRepository = EntryPointAccessors
        .fromApplication<AuthInterceptorEntryPoint>(context)
        .getTokenRepository()

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenRepository.getToken()
        val tokenAddedRequest = chain.request().putAccessToken(token?.accessToken)

        val response = chain.proceed(tokenAddedRequest)
        if (response.isAccessTokenExpired()) {
            tokenRepository.deleteToken()
            navigateToLogin()
        }
        return response
    }

    private fun Response.isAccessTokenExpired(): Boolean = (code == 401)

    private fun Request.putAccessToken(token: String?): Request =
        putHeader(ACCESS_TOKEN_HEADER, ACCESS_TOKEN_FORMAT.format(token))

    private fun Request.putHeader(
        key: String,
        value: String,
    ): Request = newBuilder().addHeader(key, value).build()

    private fun navigateToLogin() {
        val loginStartIntent = Intent(context, LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(loginStartIntent)
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
