package com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate

import com.emmsale.data.notification.updated.ChildCommentNotification
import com.emmsale.data.notification.updated.UpdatedNotification
import java.time.LocalDateTime

class ChildCommentNotificationUiState(
    id: Long,
    receiverId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val commentId: Long,
    val commentContent: String,
    val parentCommentId: Long,
    val eventId: Long,
    val eventName: String,
    val commentProfileImageUrl: String,
) : PrimaryNotificationUiState(
    id = id,
    receiverId = receiverId,
    createdAt = createdAt,
    isRead = isRead,
) {

    companion object {
        private const val MAPPING_FAILED_ERROR_MESSAGE: String =
            "[ERROR] ChildCommentNotification 타입이 아닙니다."

        fun from(updatedNotification: UpdatedNotification): PrimaryNotificationUiState {
            check(updatedNotification is ChildCommentNotification) { MAPPING_FAILED_ERROR_MESSAGE }

            return ChildCommentNotificationUiState(
                id = updatedNotification.id,
                receiverId = updatedNotification.receiverId,
                createdAt = updatedNotification.createdAt,
                isRead = updatedNotification.isRead,
                commentId = updatedNotification.childCommentId,
                commentContent = updatedNotification.childCommentContent,
                parentCommentId = updatedNotification.parentCommentId,
                eventId = updatedNotification.eventId,
                eventName = updatedNotification.eventName,
                commentProfileImageUrl = updatedNotification.commentProfileImageUrl,
            )
        }
    }
}
