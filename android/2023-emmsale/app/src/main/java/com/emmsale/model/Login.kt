package com.emmsale.model

data class Login(
    val token: Token,
    val isDoneOnboarding: Boolean,
)
