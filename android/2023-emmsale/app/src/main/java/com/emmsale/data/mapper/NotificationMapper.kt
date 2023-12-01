package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.CommentTypeNotificationResponse
import com.emmsale.data.apiModel.response.EventTypeNotificationResponse
import com.emmsale.data.apiModel.response.NotificationResponse
import com.emmsale.data.apiModel.response.NotificationResponse.NotificationType
import com.emmsale.data.model.updatedNotification.ChildCommentNotification
import com.emmsale.data.model.updatedNotification.InterestEventNotification
import com.emmsale.data.model.updatedNotification.UpdatedNotification
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JvmName("NotificationResponse")
fun List<NotificationResponse>.toData(): List<UpdatedNotification> = map { it.toData() }

fun NotificationResponse.toData(): UpdatedNotification = when (notificationType) {
    NotificationType.EVENT -> {
        val eventNotificationInformation = Json.decodeFromString<EventTypeNotificationResponse>(
            requireNotNull(extraNotificationInformation) { "이벤트 알림에 추가 정보가 없어요" },
        )
        InterestEventNotification(
            id = notificationId,
            receiverId = receiverId,
            eventId = redirectId,
            createdAt = createdAt.toLocalDateTime(),
            isRead = isRead,
            eventTitle = eventNotificationInformation.eventTitle,
        )
    }

    NotificationType.COMMENT -> {
        val commentNotificationInformation =
            Json.decodeFromString<CommentTypeNotificationResponse>(
                requireNotNull(extraNotificationInformation) { "댓글 알림에 추가 정보가 없어요" },
            )

        ChildCommentNotification(
            id = notificationId,
            receiverId = receiverId,
            createdAt = createdAt.toLocalDateTime(),
            isRead = isRead,
            parentCommentId = commentNotificationInformation.parentId,
            childCommentId = redirectId,
            childCommentContent = commentNotificationInformation.content,
            feedId = commentNotificationInformation.feedId,
            commentProfileImageUrl = commentNotificationInformation.writerProfileImageUrl,
        )
    }
}

private fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}
