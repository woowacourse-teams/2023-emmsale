package com.emmsale.data.notification

import java.time.LocalDateTime

data class RecruitmentNotification(
    val id: Long,
    val otherUid: Long,
    val myUid: Long,
    val message: String,
    val eventId: Long,
    val status: RecruitmentNotificationStatus,
    val isRead: Boolean,
    val notificationDate: LocalDateTime,
)
