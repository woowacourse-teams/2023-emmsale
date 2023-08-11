package com.emmsale.data.notification.mapper

import com.emmsale.data.notification.RecruitmentNotification
import com.emmsale.data.notification.RecruitmentNotificationApiModel

fun RecruitmentNotificationApiModel.toData(): RecruitmentNotification = RecruitmentNotification(
    id = id,
    otherUid = otherUid,
    myUid = myUid,
    message = message,
    eventId = eventId,
)

fun List<RecruitmentNotificationApiModel>.toData(): List<RecruitmentNotification> = map { it.toData() }
