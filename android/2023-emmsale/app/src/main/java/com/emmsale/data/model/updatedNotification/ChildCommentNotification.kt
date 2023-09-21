package com.emmsale.data.model.updatedNotification

import java.time.LocalDateTime

class ChildCommentNotification(
    id: Long,
    receiverId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val parentCommentId: Long,
    val childCommentId: Long,
    val childCommentContent: String,
    val eventId: Long,
    val eventName: String,
    val commentProfileImageUrl: String,
) : UpdatedNotification(
    id = id,
    receiverId = receiverId,
    createdAt = createdAt,
    isRead = isRead,
)