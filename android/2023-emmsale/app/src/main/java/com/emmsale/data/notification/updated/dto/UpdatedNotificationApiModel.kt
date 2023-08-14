package com.emmsale.data.notification.updated.dto

import kotlinx.serialization.SerialName

data class UpdatedNotificationApiModel(
    @SerialName("updateNotificationId")
    val id: Int,
    @SerialName("receiverId")
    val receiverId: Int,
    @SerialName("redirectId")
    val redirectId: Int,
    @SerialName("createdAt")
    val createdAt: String, // 2023:08:23:12:00:00
    @SerialName("type")
    val type: String,
    @SerialName("isRead")
    val isRead: Boolean,
    @SerialName("commentTypeNotification")
    val commentTypeNotification: CommentTypeNotificationApiModel? = null,
)
