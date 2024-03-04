package com.emmsale.data.mapper

import com.emmsale.data.network.apiModel.response.CommentTypeNotificationResponse
import com.emmsale.data.network.apiModel.response.EventTypeNotificationResponse
import com.emmsale.data.network.apiModel.response.NotificationResponse
import com.emmsale.data.network.apiModel.response.NotificationResponse.NotificationType
import com.emmsale.model.Comment
import com.emmsale.model.Event
import com.emmsale.model.Feed
import com.emmsale.model.Member
import com.emmsale.model.notification.ChildCommentNotification
import com.emmsale.model.notification.InterestEventNotification
import com.emmsale.model.notification.Notification
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
