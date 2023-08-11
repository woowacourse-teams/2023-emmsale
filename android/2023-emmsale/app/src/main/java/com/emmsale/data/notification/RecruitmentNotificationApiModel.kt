package com.emmsale.data.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecruitmentNotificationApiModel(
    @SerialName("notificationId")
    val id: Long,
    @SerialName("senderId")
    val otherUid: Long,
    @SerialName("receiverId")
    val myUid: Long,
    @SerialName("message")
    val message: String,
    @SerialName("eventId")
    val eventId: Long,
)
