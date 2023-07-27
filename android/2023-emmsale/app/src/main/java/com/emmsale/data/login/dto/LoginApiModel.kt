package com.emmsale.data.login.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginApiModel(
    @SerialName("memberId")
    val uid: Long,
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("newMember")
    val isNewMember: Boolean,
)
