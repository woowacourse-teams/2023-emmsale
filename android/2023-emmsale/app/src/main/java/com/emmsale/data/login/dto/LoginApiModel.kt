package com.emmsale.data.login.dto

import com.emmsale.data.login.Login
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginApiModel(
    @SerialName("memberId")
    val uid: Long,
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("onboarded")
    val onboarded: Boolean,
) {
    fun toData(): Login = Login(
        accessToken = accessToken,
        uid = uid,
        isNewMember = !onboarded,
    )
}
