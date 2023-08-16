package com.emmsale.data.notification.updated

import java.time.LocalDateTime

class ChildCommentNotification(
    id: Long,
    receiverId: Long,
    redirectId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val commentContent: String,
    val eventName: String,
    val commentProfileImageUrl: String,
) : UpdatedNotification(
    id,
    receiverId,
    redirectId,
    createdAt,
    isRead,
)
