package com.emmsale.data.mapper

import com.emmsale.data.network.apiModel.response.LoginResponse
import com.emmsale.model.Login
import com.emmsale.model.Token

fun LoginResponse.toData(): Login = Login(
    token = Token(
        uid = uid,
        accessToken = accessToken,
    ),
    isDoneOnboarding = isOnboarded,
)
