package com.emmsale.data.login

import com.emmsale.data.token.Token

data class Login(
    val token: Token,
    val isDoneOnboarding: Boolean,
)
