package com.emmsale.data.login

data class Login(
    val uid: Long,
    val accessToken: String,
    val isOnboarded: Boolean,
)
