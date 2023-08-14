package com.emmsale.data.notification.updated

import java.time.LocalDateTime

class InterestEventNotification(
    id: Int,
    receiverId: Int,
    redirectId: Int,
    createdAt: LocalDateTime,
    isRead: Boolean,
) : UpdatedNotification(
    id,
    receiverId,
    redirectId,
    createdAt,
    isRead,
)
