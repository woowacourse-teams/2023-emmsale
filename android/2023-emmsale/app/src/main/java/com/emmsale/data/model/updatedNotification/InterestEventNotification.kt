package com.emmsale.data.model.updatedNotification

import java.time.LocalDateTime

class InterestEventNotification(
    id: Long,
    receiverId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val eventId: Long,
    val eventTitle: String,
) : UpdatedNotification(
    id = id,
    receiverId = receiverId,
    createdAt = createdAt,
    isRead = isRead,
)
