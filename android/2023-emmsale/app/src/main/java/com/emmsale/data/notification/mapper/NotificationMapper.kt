package com.emmsale.data.notification.mapper

import com.emmsale.data.notification.RecruitmentNotification
import com.emmsale.data.notification.RecruitmentNotificationApiModel
import com.emmsale.data.notification.RecruitmentNotificationStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun RecruitmentNotificationApiModel.toData(): RecruitmentNotification = RecruitmentNotification(
    id = id,
    otherUid = otherUid,
    myUid = myUid,
    message = message,
    eventId = eventId,
    status = status.convertToRecruitmentNotificationStatus(),
    isRead = isRead,
    notificationDate = createdAt.toLocalDateTime(),
)

private fun String.convertToRecruitmentNotificationStatus(): RecruitmentNotificationStatus =
    when (this) {
        "ACCEPTED" -> RecruitmentNotificationStatus.ACCEPTED
        "REJECTED" -> RecruitmentNotificationStatus.REJECTED
        else -> throw IllegalArgumentException("Unknown status: $this")
    }

private fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:SS")
    return LocalDateTime.parse(this, formatter)
}

fun List<RecruitmentNotificationApiModel>.toData(): List<RecruitmentNotification> =
    map { it.toData() }
