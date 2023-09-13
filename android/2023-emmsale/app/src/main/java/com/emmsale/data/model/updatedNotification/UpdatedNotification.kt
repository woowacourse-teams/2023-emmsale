package com.emmsale.data.model.updatedNotification

import java.time.LocalDateTime

abstract class UpdatedNotification(
    val id: Long,
    val receiverId: Long,
    val createdAt: LocalDateTime,
    val isRead: Boolean,
)
