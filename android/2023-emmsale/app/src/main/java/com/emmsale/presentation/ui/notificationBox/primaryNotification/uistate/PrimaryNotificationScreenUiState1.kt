package com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate

import com.emmsale.data.notification.updated.ChildCommentNotification
import com.emmsale.data.notification.updated.InterestEventNotification
import com.emmsale.data.notification.updated.UpdatedNotification
import java.time.LocalDateTime

sealed interface PrimaryNotificationScreenUiState1 {
    data class Success(
        val recentNotifications: List<PrimaryNotificationUiState1>,
        val pastNotifications: List<PrimaryNotificationUiState1>,
    ) : PrimaryNotificationScreenUiState1 {
        companion object {
            fun create(
                recentNotifications: List<UpdatedNotification>,
                pastNotifications: List<UpdatedNotification>,
            ) = Success(
                recentNotifications = recentNotifications.map(PrimaryNotificationUiState1::from),
                pastNotifications = pastNotifications.map(PrimaryNotificationUiState1::from),
            )
        }
    }

    object Loading : PrimaryNotificationScreenUiState1

    object Error : PrimaryNotificationScreenUiState1
}

abstract class PrimaryNotificationUiState1(
    val notificationId: Long,
    val receiverId: Long,
    val createdAt: LocalDateTime,
    val isRead: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is PrimaryNotificationUiState1) return false
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

        fun from(notification: UpdatedNotification): PrimaryNotificationUiState1 =
            when (notification) {
                is ChildCommentNotification -> ChildCommentNotificationUiState1.from(notification)
                is InterestEventNotification -> InterestEventNotificationUiState1.from(notification)
                else -> throw IllegalArgumentException("${PrimaryNotificationUiState1::class.simpleName} 타입이 아닙니다.")
            }
    }
}

class ChildCommentNotificationUiState1(
    notificationId: Long,
    receiverId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val commentId: Long,
    val commentContent: String,
    val parentCommentId: Long,
    val eventId: Long,
    val eventName: String,
    val authorImageUrl: String,
) : PrimaryNotificationUiState1(
    notificationId = notificationId,
    receiverId = receiverId,
    createdAt = createdAt,
    isRead = isRead,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is ChildCommentNotificationUiState1) return false
        if (!super.equals(other)) return false

        return commentId == other.commentId &&
            commentContent == other.commentContent &&
            parentCommentId == other.parentCommentId &&
            eventId == other.eventId &&
            eventName == other.eventName &&
            authorImageUrl == other.authorImageUrl
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = PRIME * result + commentId.hashCode()
        result = PRIME * result + commentContent.hashCode()
        result = PRIME * result + parentCommentId.hashCode()
        result = PRIME * result + eventId.hashCode()
        result = PRIME * result + eventName.hashCode()
        result = PRIME * result + authorImageUrl.hashCode()
        return result
    }

    companion object {
        fun from(notification: ChildCommentNotification) = ChildCommentNotificationUiState1(
            notificationId = notification.id,
            receiverId = notification.receiverId,
            createdAt = notification.createdAt,
            isRead = notification.isRead,
            commentId = notification.childCommentId,
            commentContent = notification.childCommentContent,
            parentCommentId = notification.parentCommentId,
            eventId = notification.eventId,
            eventName = notification.eventName,
            authorImageUrl = notification.commentProfileImageUrl,
        )
    }
}

class InterestEventNotificationUiState1(
    notificationId: Long,
    receiverId: Long,
    createdAt: LocalDateTime,
    isRead: Boolean,
    val eventId: Long,
) : PrimaryNotificationUiState1(
    notificationId = notificationId,
    receiverId = receiverId,
    createdAt = createdAt,
    isRead = isRead,
) {

    override fun equals(other: Any?): Boolean {
        if (other !is InterestEventNotificationUiState1) return false
        if (!super.equals(other)) return false

        return eventId == other.eventId
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = PRIME * result + eventId.hashCode()
        return result
    }

    companion object {
        fun from(notification: InterestEventNotification) = InterestEventNotificationUiState1(
            notificationId = notification.id,
            receiverId = notification.receiverId,
            createdAt = notification.createdAt,
            isRead = notification.isRead,
            eventId = notification.eventId,
        )
    }
}
