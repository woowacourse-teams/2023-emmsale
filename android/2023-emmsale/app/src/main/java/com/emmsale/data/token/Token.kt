package com.emmsale.data.token

data class Token(
    val uid: Long,
    val accessToken: String,
    val isAutoLogin: Boolean,
)
