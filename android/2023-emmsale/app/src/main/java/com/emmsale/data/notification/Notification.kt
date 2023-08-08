package com.emmsale.data.notification

data class Notification(
    val id: Long,
    val otherUid: Long,
    val myUid: Long,
    val message: String,
    val eventId: Long,
)
