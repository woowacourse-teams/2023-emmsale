package com.emmsale.data.notification

data class RecruitmentNotification(
    val id: Long,
    val otherUid: Long,
    val myUid: Long,
    val message: String,
    val eventId: Long,
)
