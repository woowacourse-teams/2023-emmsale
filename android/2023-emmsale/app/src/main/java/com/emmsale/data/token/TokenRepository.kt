package com.emmsale.data.token

interface TokenRepository {
    fun saveToken(token: Token)
    fun getToken(): Token?
}
