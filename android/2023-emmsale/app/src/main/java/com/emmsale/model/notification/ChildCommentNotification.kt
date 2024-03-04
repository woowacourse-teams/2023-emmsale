package com.emmsale.model.notification

import com.emmsale.model.Comment
import java.time.LocalDateTime

class ChildCommentNotification(
    id: Long,
    receiverId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val comment: Comment,
) : Notification(
    id = id,
    receiverId = receiverId,
    createdAt = createdAt,
    isRead = isRead,
)
