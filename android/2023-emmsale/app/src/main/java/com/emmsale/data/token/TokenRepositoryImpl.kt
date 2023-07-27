package com.emmsale.data.token

import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val preference: SharedPreferences,
) : TokenRepository {

    override suspend fun saveToken(token: Token) = withContext(dispatcher) {
        preference.edit().putLong(UID_KEY, token.uid).apply()
        preference.edit().putString(ACCESS_TOKEN_KEY, token.accessToken).apply()
        // preference.edit().putString(REFRESH_TOKEN_KEY, token.accessToken).apply()
    }

    override suspend fun getToken(): Token? = withContext(dispatcher) {
        val uid = preference.getLong(UID_KEY, DEFAULT_UID_VALUE)
        val accessToken = preference.getString(ACCESS_TOKEN_KEY, DEFAULT_TOKEN_VALUE)
        // val refreshToken = preference.getString(REFRESH_TOKEN_KEY, DEFAULT_TOKEN_VALUE)

        if (uid == DEFAULT_UID_VALUE) return@withContext null
        if (accessToken == null || accessToken == DEFAULT_TOKEN_VALUE) return@withContext null
        // if (refreshToken == null || refreshToken == DEFAULT_TOKEN_VALUE) return@withContext null

        Token(uid, accessToken)
    }

    companion object {
        private const val UID_KEY = "uid_key"
        private const val ACCESS_TOKEN_KEY = "access_token_key"
        // private const val REFRESH_TOKEN_KEY = "refresh_token_key"

        private const val DEFAULT_UID_VALUE = -1L
        private const val DEFAULT_TOKEN_VALUE = "default"
    }
}

