package com.emmsale.data.model.notification

import java.time.LocalDateTime

sealed class Notification(
    val id: Long,
    val receiverId: Long,
    val createdAt: LocalDateTime,
    val isRead: Boolean,
) {

    fun read(): Notification = when (this) {
        is ChildCommentNotification -> ChildCommentNotification(
            id = id,
            receiverId = receiverId,
            createdAt = createdAt,
            isRead = true,
            comment = comment,
        )

        is InterestEventNotification -> InterestEventNotification(
            id = id,
            receiverId = receiverId,
            createdAt = createdAt,
            isRead = true,
            event = event,
        )
    }
}
