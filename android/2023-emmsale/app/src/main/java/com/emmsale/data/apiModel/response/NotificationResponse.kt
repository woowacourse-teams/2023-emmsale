package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    @SerialName("notificationId")
    val notificationId: Long,
    @SerialName("receiverId")
    val receiverId: Long,
    @SerialName("redirectId")
    val redirectId: Long,
    @SerialName("createdAt")
    val createdAt: String, // 2023:08:23:12:00:00
    @SerialName("type")
    val notificationType: NotificationType,
    @SerialName("isRead")
    val isRead: Boolean,
    @SerialName("notificationInformation")
    val additionalInformation: String? = null,
) {
    enum class NotificationType {
        @SerialName("EVENT")
        EVENT,

        @SerialName("COMMENT")
        COMMENT,
    }
}

@Serializable
data class CommentTypeNotificationResponse(
    @SerialName("writer")
    val writerName: String,
    @SerialName("writerImageUrl")
    val writerProfileImageUrl: String,
    @SerialName("parentCommentId")
    val parentId: Long = -1L,
    @SerialName("feedId")
    val feedId: Long = -1L,
    @SerialName("content")
    val content: String = "",
)

@Serializable
data class EventTypeNotificationResponse(
    @SerialName("title")
    val eventTitle: String,
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
