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
    val isOnboarded: Boolean,
) {
    fun toData(): Login = Login(
        accessToken = accessToken,
        uid = uid,
        isOnboarded = isOnboarded,
    )
}
