package com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate

import com.emmsale.data.notification.updated.InterestEventNotification
import com.emmsale.data.notification.updated.UpdatedNotification
import java.time.LocalDateTime

class InterestEventNotificationUiState(
    id: Long,
    receiverId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val eventId: Long,
) : PrimaryNotificationUiState(
    id,
    receiverId,
    createdAt,
    isRead,
) {
    companion object {
        private val MAPPING_FAILED_ERROR_MESSAGE: String =
            "${InterestEventNotification::javaClass.name} 타입이 아닙니다."

        fun from(updatedNotification: UpdatedNotification): PrimaryNotificationUiState {
            check(updatedNotification is InterestEventNotification) { MAPPING_FAILED_ERROR_MESSAGE }

            return InterestEventNotificationUiState(
                id = updatedNotification.id,
                receiverId = updatedNotification.receiverId,
                eventId = updatedNotification.redirectId,
                createdAt = updatedNotification.createdAt,
                isRead = updatedNotification.isPast,
            )
        }
    }
}
