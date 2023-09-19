package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatedNotificationResponse(
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
    val commentTypeNotification: CommentTypeNotificationResponse? = null,
)

@Serializable
data class CommentTypeNotificationResponse(
    @SerialName("content")
    val childCommentContent: String,
    @SerialName("eventName")
    val eventName: String,
    @SerialName("commenterImageUrl")
    val commentProfileImageUrl: String,
    @SerialName("parentId")
    val parentId: Long = -1L,
    @SerialName("eventId")
    val eventId: Long = -1L,
)

@Serializable
data class RecruitmentNotificationResponse(
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

@Serializable
data class NotificationReportResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("reporterId")
    val reporterId: Long,
    @SerialName("reportedId")
    val reportedId: Long,
    @SerialName("type")
    val type: String,
    @SerialName("contentId")
    val contentId: Long,
    @SerialName("createdAt")
    val createdAt: String,
)
