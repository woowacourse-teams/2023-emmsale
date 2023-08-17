package com.emmsale.data.notification.updated

import java.time.LocalDateTime

class InterestEventNotification(
    id: Long,
    receiverId: Long,
    eventId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
) : UpdatedNotification(
    id,
    receiverId,
    eventId,
    createdAt,
    isRead,
)
