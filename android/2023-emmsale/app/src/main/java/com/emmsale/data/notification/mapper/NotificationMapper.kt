package com.emmsale.data.notification.mapper

import com.emmsale.data.notification.Notification
import com.emmsale.data.notification.NotificationApiModel

fun NotificationApiModel.toData(): Notification = Notification(
    id = id,
    otherUid = otherUid,
    myUid = myUid,
    message = message,
    eventId = eventId,
)

fun Notification.toApiModel(): NotificationApiModel = NotificationApiModel(
    id = id,
    otherUid = otherUid,
    myUid = myUid,
    message = message,
    eventId = eventId,
)

fun List<NotificationApiModel>.toData(): List<Notification> = map { it.toData() }
