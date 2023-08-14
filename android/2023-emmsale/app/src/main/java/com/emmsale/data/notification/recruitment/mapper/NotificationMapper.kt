package com.emmsale.data.notification.recruitment.mapper

import com.emmsale.data.notification.recruitment.RecruitmentNotification
import com.emmsale.data.notification.recruitment.dto.RecruitmentNotificationApiModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun RecruitmentNotificationApiModel.toData(): RecruitmentNotification = RecruitmentNotification(
    id = id,
    senderUid = senderUid,
    receiverUid = receiverUid,
    message = message,
    eventId = eventId,
    status = status.toRecruitmentStatus(),
    isRead = isRead,
    notificationDate = createdAt.toLocalDateTime(),
)

private fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}

fun List<RecruitmentNotificationApiModel>.toData(): List<RecruitmentNotification> =
    map { it.toData() }
