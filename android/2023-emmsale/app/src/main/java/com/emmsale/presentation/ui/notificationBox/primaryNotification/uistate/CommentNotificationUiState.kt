package com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate

import com.emmsale.data.notification.updated.ChildCommentNotification
import com.emmsale.data.notification.updated.UpdatedNotification
import java.time.LocalDateTime

class CommentNotificationUiState(
    id: Int,
    receiverId: Int,
    redirectId: Int,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val commentContent: String,
    val eventName: String,
    val commentProfileImageUrl: String,
) : PrimaryNotificationUiState(
    id,
    receiverId,
    redirectId,
    createdAt,
    isRead,
) {

    companion object {
        private val MAPPING_FAILED_ERROR_MESSAGE: String =
            "${ChildCommentNotification::javaClass.name} 타입이 아닙니다."

        fun from(updatedNotification: UpdatedNotification): PrimaryNotificationUiState {
            check(updatedNotification is ChildCommentNotification) { MAPPING_FAILED_ERROR_MESSAGE }

            return CommentNotificationUiState(
                id = updatedNotification.id,
                receiverId = updatedNotification.receiverId,
                redirectId = updatedNotification.redirectId,
                createdAt = updatedNotification.createdAt,
                isRead = updatedNotification.isPast,
                commentContent = updatedNotification.commentContent,
                eventName = updatedNotification.eventName,
                commentProfileImageUrl = updatedNotification.commentProfileImageUrl,
            )
        }
    }
}
