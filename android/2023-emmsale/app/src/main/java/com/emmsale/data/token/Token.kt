package com.emmsale.data.token

import com.emmsale.data.login.Login

data class Token(
    val uid: Long,
    val accessToken: String,
) {
    companion object {
        fun from(login: Login): Token = Token(
            accessToken = login.accessToken,
            uid = login.uid,
        )
    }
}
