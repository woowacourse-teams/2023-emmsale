package com.emmsale.data.notification.updated.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatedNotificationApiModel(
    @SerialName("updateNotificationId")
    val id: Long,
    @SerialName("receiverId")
    val receiverId: Long,
    @SerialName("redirectId")
    val redirectId: Long,
    @SerialName("createdAt")
    val createdAt: String, // 2023:08:23:12:00:00
    @SerialName("type")
    val type: String,
    @SerialName("isRead")
    val isRead: Boolean,
    @SerialName("commentTypeNotification")
    val commentTypeNotification: CommentTypeNotificationApiModel? = null,
)
