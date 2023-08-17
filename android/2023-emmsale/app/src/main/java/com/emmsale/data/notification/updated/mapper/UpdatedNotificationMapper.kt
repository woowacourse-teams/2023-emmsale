package com.emmsale.data.notification.updated.mapper

import com.emmsale.data.notification.updated.ChildCommentNotification
import com.emmsale.data.notification.updated.InterestEventNotification
import com.emmsale.data.notification.updated.UpdatedNotification
import com.emmsale.data.notification.updated.dto.UpdatedNotificationApiModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val EVENT_TYPE = "EVENT"
private const val COMMENT_TYPE = "COMMENT"

fun List<UpdatedNotificationApiModel>.toData(): List<UpdatedNotification> = map { it.toData() }

fun UpdatedNotificationApiModel.toData(): UpdatedNotification = when (type) {
    EVENT_TYPE -> InterestEventNotification(
        id = id,
        receiverId = receiverId,
        eventId = redirectId,
        createdAt = createdAt.toLocalDateTime(),
        isRead = isRead,
    )

    COMMENT_TYPE -> ChildCommentNotification(
        id = id,
        receiverId = receiverId,
        commentId = redirectId,
        createdAt = createdAt.toLocalDateTime(),
        isRead = isRead,
        commentContent = commentTypeNotification?.childCommentContent ?: "",
        eventName = commentTypeNotification?.eventName ?: "",
        commentProfileImageUrl = commentTypeNotification?.commentProfileImageUrl ?: "",
    )

    else -> throw IllegalArgumentException("$type : 알 수 없는 알림 타입입니다.")
}

private fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}
