package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.RecruitmentNotificationResponse
import com.emmsale.data.model.RecruitmentNotification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun RecruitmentNotificationResponse.toData(): RecruitmentNotification = RecruitmentNotification(
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

fun List<RecruitmentNotificationResponse>.toData(): List<RecruitmentNotification> =
    map { it.toData() }
