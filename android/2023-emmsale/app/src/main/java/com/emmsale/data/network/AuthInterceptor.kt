package com.emmsale.data.network

import android.content.Context
import android.content.Intent
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.ui.login.LoginActivity
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val context: Context,
    private val tokenRepository: TokenRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenRepository.getToken()?.accessToken
        val tokenAddedRequest = chain.request().putAccessToken(token)

        val response = chain.proceed(tokenAddedRequest)
        if (response.isAccessTokenInvalid()) {
            navigateToLogin()
        }
        return response
    }

    private fun Response.isAccessTokenInvalid(): Boolean = (code == UNAUTHORIZED_ERROR_CODE)

    private fun Request.putAccessToken(token: String?): Request =
        putHeader(ACCESS_TOKEN_HEADER, ACCESS_TOKEN_FORMAT.format(token))

    private fun Request.putHeader(
        key: String,
        value: String,
    ): Request = newBuilder().addHeader(key, value).build()

    private fun navigateToLogin() { // bad practice but now approved
        val loginStartIntent = Intent(context, LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(loginStartIntent)
    }

    companion object {
        private const val ACCESS_TOKEN_HEADER = "authorization"
        private const val ACCESS_TOKEN_FORMAT = "Bearer %s"
        private const val UNAUTHORIZED_ERROR_CODE = 401
    }
}
