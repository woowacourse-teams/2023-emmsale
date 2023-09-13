package com.emmsale.data.repository

import com.emmsale.data.model.Token

interface TokenRepository {
    suspend fun saveToken(token: Token)
    suspend fun getToken(): Token?
    fun getMyUid(): Long?
    suspend fun deleteToken()
}
