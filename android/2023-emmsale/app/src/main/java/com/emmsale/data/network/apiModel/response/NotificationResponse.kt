package com.emmsale.data.network.apiModel.response

import com.emmsale.data.network.apiModel.serializer.DateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class NotificationResponse(
    @SerialName("notificationId")
    val notificationId: Long,
    @SerialName("receiverId")
    val receiverId: Long,
    @SerialName("redirectId")
    val redirectId: Long,
    @SerialName("createdAt")
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @SerialName("type")
    val notificationType: NotificationType,
    @SerialName("isRead")
    val isRead: Boolean,
    @SerialName("notificationInformation")
    val extraNotificationInformation: String? = null,
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
