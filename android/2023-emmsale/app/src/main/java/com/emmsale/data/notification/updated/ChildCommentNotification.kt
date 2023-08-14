package com.emmsale.data.notification.updated

import java.time.LocalDateTime

class ChildCommentNotification(
    id: Int,
    receiverId: Int,
    redirectId: Int,
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
