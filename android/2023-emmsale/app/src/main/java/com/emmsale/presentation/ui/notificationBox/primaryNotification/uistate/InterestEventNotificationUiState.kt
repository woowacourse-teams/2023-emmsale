package com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate

import com.emmsale.data.model.updatedNotification.InterestEventNotification
import java.time.LocalDateTime

class InterestEventNotificationUiState(
    notificationId: Long,
    receiverId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val eventId: Long,
) : PrimaryNotificationUiState(
    notificationId = notificationId,
    receiverId = receiverId,
    createdAt = createdAt,
    isRead = isRead,
) {

    override fun equals(other: Any?): Boolean {
        if (other !is InterestEventNotificationUiState) return false
        if (!super.equals(other)) return false

        return eventId == other.eventId
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = PRIME * result + eventId.hashCode()
        return result
    }

    companion object {
        fun from(notification: InterestEventNotification) = InterestEventNotificationUiState(
            notificationId = notification.id,
            receiverId = notification.receiverId,
            createdAt = notification.createdAt,
            isRead = notification.isRead,
            eventId = notification.eventId,
        )
    }
}
