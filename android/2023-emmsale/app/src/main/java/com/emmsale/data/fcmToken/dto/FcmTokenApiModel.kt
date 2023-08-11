package com.emmsale.data.fcmToken.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenApiModel(
    @SerialName("memberId")
    val uid: Long,
    @SerialName("token")
    val token: String,
)
