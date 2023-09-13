package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenApiModel(
    @SerialName("memberId")
    val uid: Long,
    @SerialName("token")
    val token: String,
)
