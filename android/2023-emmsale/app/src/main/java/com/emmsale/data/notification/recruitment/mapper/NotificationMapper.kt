package com.emmsale.data.notification.recruitment.mapper

import com.emmsale.data.notification.recruitment.RecruitmentNotification
import com.emmsale.data.notification.recruitment.RecruitmentStatus
import com.emmsale.data.notification.recruitment.dto.RecruitmentNotificationApiModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun RecruitmentNotificationApiModel.toData(): RecruitmentNotification = RecruitmentNotification(
    id = id,
    senderUid = senderUid,
    receiverUid = receiverUid,
    message = message,
    eventId = eventId,
    status = status.convertToRecruitmentNotificationStatus(),
    isRead = isRead,
    notificationDate = createdAt.toLocalDateTime(),
)

private fun String.convertToRecruitmentNotificationStatus(): RecruitmentStatus =
    when (this) {
        "IN_PROGRESS" -> RecruitmentStatus.IN_PROGRESS
        "ACCEPTED" -> RecruitmentStatus.ACCEPTED
        "REJECTED" -> RecruitmentStatus.REJECTED
        else -> throw IllegalArgumentException("Unknown status: $this")
    }

private fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:SS")
    return LocalDateTime.parse(this, formatter)
}

fun List<RecruitmentNotificationApiModel>.toData(): List<RecruitmentNotification> =
    map { it.toData() }
