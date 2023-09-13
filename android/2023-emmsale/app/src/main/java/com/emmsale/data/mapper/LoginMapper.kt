package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.LoginApiModel
import com.emmsale.data.model.Login
import com.emmsale.data.model.Token

fun LoginApiModel.toData(): Login = Login(
    token = Token(
        uid = uid,
        accessToken = accessToken,
    ),
    isDoneOnboarding = isOnboarded,
)
