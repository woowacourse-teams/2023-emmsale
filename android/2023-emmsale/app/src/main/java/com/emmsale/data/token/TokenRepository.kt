package com.emmsale.data.token

interface TokenRepository {
    suspend fun saveToken(token: Token)
    suspend fun getToken(): Token?
}
