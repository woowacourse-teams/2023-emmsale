package com.emmsale.data.login

import com.emmsale.data.login.dto.LoginApiModel

data class Login(
    val uid: Long,
    val accessToken: String,
    val isNewMember: Boolean,
) {
    companion object {
        fun from(apiModel: LoginApiModel): Login = Login(
            accessToken = apiModel.accessToken,
            uid = apiModel.uid,
            isNewMember = apiModel.isNewMember,
        )
    }
}
