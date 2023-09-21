package com.emmsale.data.repository.interfaces

import com.emmsale.data.model.Token

interface TokenRepository {
    fun saveToken(token: Token)
    fun getToken(): Token?
    fun getMyUid(): Long?
    fun deleteToken()
}
