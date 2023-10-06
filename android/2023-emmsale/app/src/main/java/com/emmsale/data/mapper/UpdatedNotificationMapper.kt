package com.emmsale.data.mapper

import android.util.Log
import com.emmsale.data.apiModel.response.CommentTypeNotificationResponse
import com.emmsale.data.apiModel.response.EventTypeNotificationResponse
import com.emmsale.data.apiModel.response.UpdatedNotificationResponse
import com.emmsale.data.model.updatedNotification.ChildCommentNotification
import com.emmsale.data.model.updatedNotification.InterestEventNotification
import com.emmsale.data.model.updatedNotification.UpdatedNotification
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val EVENT_TYPE = "EVENT"
private const val COMMENT_TYPE = "COMMENT"

fun List<UpdatedNotificationResponse>.toData(): List<UpdatedNotification> = map { it.toData() }

fun UpdatedNotificationResponse.toData(): UpdatedNotification = when (type) {
    EVENT_TYPE -> {
        val eventNotificationInformation =
            Json.decodeFromString<EventTypeNotificationResponse>(
                notificationInformation ?: throw IllegalArgumentException("이벤트 알림에 정보가 없어요"),
            )
        InterestEventNotification(
            id = id,
            receiverId = receiverId,
            eventId = redirectId,
            createdAt = createdAt.toLocalDateTime(),
            isRead = isRead,
            eventTitle = eventNotificationInformation.eventTitle,
        )
    }

    COMMENT_TYPE -> {
        val commentNotificationInformation =
            Json.decodeFromString<CommentTypeNotificationResponse>(
                notificationInformation ?: throw IllegalArgumentException("코멘트 알림에 정보가 없어요"),
            )
        Log.d("wooseok", commentNotificationInformation.toString())
        ChildCommentNotification(
            id = id,
            receiverId = receiverId,
            createdAt = createdAt.toLocalDateTime(),
            isRead = isRead,
            parentCommentId = commentNotificationInformation.parentId ?: 0,
            childCommentId = redirectId,
            childCommentContent = commentNotificationInformation.content ?: "",
            feedId = commentNotificationInformation.feedId ?: 0,
            commentProfileImageUrl = commentNotificationInformation.commentProfileImageUrl ?: "",
        )
    }

    else -> throw IllegalArgumentException("$type : 알 수 없는 알림 타입입니다.")
}

private fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}
