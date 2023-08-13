package com.emmsale.data.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecruitmentNotificationApiModel(
    @SerialName("notificationId")
    val id: Long,
    @SerialName("senderId")
    val senderUid: Long,
    @SerialName("receiverId")
    val receiverUid: Long,
    @SerialName("message")
    val message: String,
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("status")
    val status: String,
    @SerialName("isRead")
    val isRead: Boolean,
    @SerialName("createdAt")
    val createdAt: String,
)
