package com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate

import com.emmsale.data.notification.updated.ChildCommentNotification
import com.emmsale.data.notification.updated.InterestEventNotification
import com.emmsale.data.notification.updated.UpdatedNotification
import java.time.LocalDateTime

abstract class PrimaryNotificationUiState(
    val id: Long,
    val receiverId: Long,
    val redirectId: Long,
    val createdAt: LocalDateTime,
    val isRead: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PrimaryNotificationUiState) return false

        if (id != other.id) return false
        if (receiverId != other.receiverId) return false
        if (redirectId != other.redirectId) return false
        if (createdAt != other.createdAt) return false
        if (isRead != other.isRead) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + receiverId
        result = 31 * result + redirectId
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + isRead.hashCode()
        return result.toInt()
    }

    companion object {
        fun from(notification: UpdatedNotification, eventId: Long): PrimaryNotificationUiState =
            when (notification) {
                is InterestEventNotification -> InterestEventNotificationUiState.from(notification)
                is ChildCommentNotification -> CommentNotificationUiState.from(
                    updatedNotification = notification,
                    eventId = eventId,
                )

                else -> throw IllegalArgumentException("${notification::javaClass.name} 타입이 아닙니다.")
            }
    }
}
