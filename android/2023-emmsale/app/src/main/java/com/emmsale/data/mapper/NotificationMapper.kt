package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.CommentTypeNotificationResponse
import com.emmsale.data.apiModel.response.EventTypeNotificationResponse
import com.emmsale.data.apiModel.response.NotificationResponse
import com.emmsale.data.apiModel.response.NotificationResponse.NotificationType
import com.emmsale.data.model.Comment
import com.emmsale.data.model.Event
import com.emmsale.data.model.Feed
import com.emmsale.data.model.Member
import com.emmsale.data.model.notification.ChildCommentNotification
import com.emmsale.data.model.notification.InterestEventNotification
import com.emmsale.data.model.notification.Notification
import kotlinx.serialization.json.Json

@JvmName("NotificationResponse")
fun List<NotificationResponse>.toData(): List<Notification> = map { it.toData() }

fun NotificationResponse.toData(): Notification = when (notificationType) {
    NotificationType.EVENT -> {
        val eventNotificationInformation = Json.decodeFromString<EventTypeNotificationResponse>(
            requireNotNull(extraNotificationInformation) { "이벤트 알림에 추가 정보가 없어요" },
        )
        InterestEventNotification(
            id = notificationId,
            receiverId = receiverId,
            createdAt = createdAt,
            isRead = isRead,
            event = Event(
                id = redirectId,
                name = eventNotificationInformation.eventTitle,
            ),
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
            createdAt = createdAt,
            isRead = isRead,
            comment = Comment(
                id = redirectId,
                content = commentNotificationInformation.content,
                parentCommentId = commentNotificationInformation.parentId,
                feed = Feed(id = commentNotificationInformation.feedId),
                writer = Member(
                    name = commentNotificationInformation.writerName,
                    profileImageUrl = commentNotificationInformation.writerProfileImageUrl,
                ),
            ),
        )
    }
}
