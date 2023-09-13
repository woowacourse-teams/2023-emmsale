package com.emmsale.data.model

import java.time.LocalDateTime

data class RecruitmentNotification(
    val id: Long,
    val senderUid: Long,
    val receiverUid: Long,
    val message: String,
    val eventId: Long,
    val status: RecruitmentStatus,
    val isRead: Boolean,
    val notificationDate: LocalDateTime,
)
