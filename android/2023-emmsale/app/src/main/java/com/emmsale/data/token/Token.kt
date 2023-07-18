package com.emmsale.data.token

import com.emmsale.data.login.Login

data class Token(
    val uid: Long,
    val accessToken: String,
    val refreshToken: String,
) {
    companion object {
        fun from(login: Login): Token = Token(
            accessToken = login.accessToken,
            refreshToken = login.refreshToken,
            uid = login.uid,
        )
    }
}
