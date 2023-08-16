package com.emmsale.data.notification.updated

import java.time.LocalDateTime

abstract class UpdatedNotification(
    val id: Long,
    val receiverId: Long,
    val redirectId: Long,
    val createdAt: LocalDateTime,
    val isPast: Boolean,
)
