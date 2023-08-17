package com.emmsale.data.notification.updated

import java.time.LocalDateTime

class ChildCommentNotification(
    id: Long,
    receiverId: Long,
    commentId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val commentContent: String,
    val eventName: String,
    val commentProfileImageUrl: String,
) : UpdatedNotification(
    id,
    receiverId,
    commentId,
    createdAt,
    isRead,
)
