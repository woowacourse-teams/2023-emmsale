package com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate

import com.emmsale.data.model.updatedNotification.ChildCommentNotification
import com.emmsale.data.model.updatedNotification.InterestEventNotification
import com.emmsale.data.model.updatedNotification.UpdatedNotification
import java.time.LocalDateTime

abstract class PrimaryNotificationUiState(
    val notificationId: Long,
    val receiverId: Long,
    val createdAt: LocalDateTime,
    val isRead: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is PrimaryNotificationUiState) return false
        return notificationId == other.notificationId &&
            receiverId == other.receiverId &&
            createdAt == other.createdAt &&
            isRead == other.isRead
    }

    override fun hashCode(): Int {
        var result = 1
        result = PRIME * result + notificationId.hashCode()
        result = PRIME * result + receiverId.hashCode()
        result = PRIME * result + createdAt.hashCode()
        result = PRIME * result + isRead.hashCode()
        return result
    }

    companion object {
        @JvmStatic
        protected val PRIME = 31

        fun from(notification: UpdatedNotification): PrimaryNotificationUiState =
            when (notification) {
                is ChildCommentNotification -> ChildCommentNotificationUiState.from(notification)
                is InterestEventNotification -> InterestEventNotificationUiState.from(notification)
                else -> throw IllegalArgumentException("${PrimaryNotificationUiState::class.simpleName} 타입이 아닙니다.")
            }
    }
}
