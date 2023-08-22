package com.emmsale.data.login.mapper

import com.emmsale.data.login.Login
import com.emmsale.data.login.dto.LoginApiModel
import com.emmsale.data.token.Token

fun LoginApiModel.toData(): Login = Login(
    token = Token(
        uid = uid,
        accessToken = accessToken,
    ),
    isDoneOnboarding = isOnboarded,
)
