package com.emmsale.presentation.ui.primaryNotificationList.uiState

import com.emmsale.data.model.updatedNotification.ChildCommentNotification
import java.time.LocalDateTime

class ChildCommentNotificationUiState(
    notificationId: Long,
    receiverId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val commentId: Long,
    val commentContent: String,
    val parentCommentId: Long,
    val feedId: Long,
    val commenterProfileImageUrl: String,
) : PrimaryNotificationUiState(
    notificationId = notificationId,
    receiverId = receiverId,
    createdAt = createdAt,
    isRead = isRead,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is ChildCommentNotificationUiState) return false
        if (!super.equals(other)) return false

        return commentId == other.commentId &&
            commentContent == other.commentContent &&
            parentCommentId == other.parentCommentId &&
            feedId == other.feedId &&
            commenterProfileImageUrl == other.commenterProfileImageUrl
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = PRIME * result + commentId.hashCode()
        result = PRIME * result + commentContent.hashCode()
        result = PRIME * result + parentCommentId.hashCode()
        result = PRIME * result + feedId.hashCode()
        result = PRIME * result + commenterProfileImageUrl.hashCode()
        return result
    }

    companion object {
        fun from(notification: ChildCommentNotification) = ChildCommentNotificationUiState(
            notificationId = notification.id,
            receiverId = notification.receiverId,
            createdAt = notification.createdAt,
            isRead = notification.isRead,
            commentId = notification.childCommentId,
            commentContent = notification.childCommentContent,
            parentCommentId = notification.parentCommentId,
            feedId = notification.feedId,
            commenterProfileImageUrl = notification.commentProfileImageUrl,
        )
    }
}
