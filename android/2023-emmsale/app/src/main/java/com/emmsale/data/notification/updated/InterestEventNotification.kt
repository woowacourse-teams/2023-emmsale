package com.emmsale.data.notification.updated

import java.time.LocalDateTime

class InterestEventNotification(
    id: Long,
    receiverId: Long,
    redirectId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
) : UpdatedNotification(
    id,
    receiverId,
    redirectId,
    createdAt,
    isRead,
)
