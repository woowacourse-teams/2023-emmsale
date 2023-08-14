package com.emmsale.data.notification.updated

import java.time.LocalDateTime

abstract class UpdatedNotification(
    val id: Int,
    val receiverId: Int,
    val redirectId: Int,
    val createdAt: LocalDateTime,
    val isRead: Boolean,
)
