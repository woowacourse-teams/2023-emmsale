package com.emmsale.data.fcmToken.dto

import com.emmsale.data.fcmToken.FcmToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenApiModel(
    @SerialName("memberId")
    val uid: Long,
    @SerialName("token")
    val token: String,
) {
    companion object {
        fun from(fcmToken: FcmToken): FcmTokenApiModel = FcmTokenApiModel(
            uid = fcmToken.uid,
            token = fcmToken.token,
        )
    }
}
