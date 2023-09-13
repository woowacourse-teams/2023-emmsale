package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("memberId")
    val uid: Long,
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("onboarded")
    val isOnboarded: Boolean,
)
