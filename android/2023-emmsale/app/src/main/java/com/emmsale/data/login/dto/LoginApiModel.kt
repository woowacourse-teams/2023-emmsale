package com.emmsale.data.login.dto

data class LoginApiModel(
    val accessToken: String,
    val refreshToken: String,
    val uid: Long,
    val isRegistered: Boolean,
)
