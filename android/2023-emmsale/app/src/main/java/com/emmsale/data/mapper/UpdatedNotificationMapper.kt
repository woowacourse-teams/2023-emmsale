package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.UpdatedNotificationApiModel
import com.emmsale.data.model.updatedNotification.ChildCommentNotification
import com.emmsale.data.model.updatedNotification.InterestEventNotification
import com.emmsale.data.model.updatedNotification.UpdatedNotification
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
        createdAt = createdAt.toLocalDateTime(),
        isRead = isRead,
        parentCommentId = commentTypeNotification?.parentId ?: 0,
        childCommentId = redirectId,
        childCommentContent = commentTypeNotification?.childCommentContent ?: "",
        eventId = commentTypeNotification?.eventId ?: 0,
        eventName = commentTypeNotification?.eventName ?: "",
        commentProfileImageUrl = commentTypeNotification?.commentProfileImageUrl ?: "",
    )

    else -> throw IllegalArgumentException("$type : 알 수 없는 알림 타입입니다.")
}

private fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}
