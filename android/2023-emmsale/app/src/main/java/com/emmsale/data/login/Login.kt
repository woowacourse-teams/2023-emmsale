package com.emmsale.data.login

import com.emmsale.data.login.dto.LoginApiModel

data class Login(
    val uid: Long,
    val accessToken: String,
    val refreshToken: String,
    val isRegistered: Boolean,
) {
    companion object {
        fun from(apiModel: LoginApiModel): Login = Login(
            accessToken = apiModel.accessToken,
            refreshToken = apiModel.refreshToken,
            uid = apiModel.uid,
            isRegistered = apiModel.isRegistered,
        )
    }
}
