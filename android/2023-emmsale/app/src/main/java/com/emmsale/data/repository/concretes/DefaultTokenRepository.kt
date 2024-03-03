package com.emmsale.data.repository.concretes

import android.content.SharedPreferences
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.model.Token
import javax.inject.Inject

class DefaultTokenRepository @Inject constructor(
    private val preference: SharedPreferences,
) : TokenRepository {
    private val preferenceEditor = preference.edit()

    override fun saveToken(token: Token) {
        preferenceEditor.putLong(UID_KEY, token.uid).apply()
        preferenceEditor.putString(ACCESS_TOKEN_KEY, token.accessToken).apply()
    }

    override fun getToken(): Token? {
        val uid = preference.getLong(UID_KEY, DEFAULT_UID_VALUE)
        val accessToken = preference.getString(ACCESS_TOKEN_KEY, DEFAULT_TOKEN_VALUE)

        if (uid == DEFAULT_UID_VALUE) return null
        if (accessToken == null || accessToken == DEFAULT_TOKEN_VALUE) return null

        return Token(uid, accessToken)
    }

    override fun getMyUid(): Long? {
        val uid = preference.getLong(UID_KEY, DEFAULT_UID_VALUE)
        if (uid == DEFAULT_UID_VALUE) return null

        return uid
    }

    override fun deleteToken() {
        preference.edit().remove(UID_KEY).remove(ACCESS_TOKEN_KEY).apply()
    }

    companion object {
        private const val UID_KEY = "uid_key"
        private const val DEFAULT_UID_VALUE = -1L

        private const val ACCESS_TOKEN_KEY = "access_token_key"
        private const val DEFAULT_TOKEN_VALUE = "default"
    }
}
