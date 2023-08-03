package com.emmsale.data.notification

class Notification(
    val id: Long,
    val otherUid: Long,
    val myUid: Long,
    val message: String,
    val eventId: Long,
)
