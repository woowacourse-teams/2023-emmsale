package com.emmsale.data.model.notification

import com.emmsale.data.model.Event
import java.time.LocalDateTime

class InterestEventNotification(
    id: Long,
    receiverId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val event: Event,
) : Notification(
    id = id,
    receiverId = receiverId,
    createdAt = createdAt,
    isRead = isRead,
)
