package com.emmsale.presentation.ui.notificationBox.uistate

import com.emmsale.data.notification.Notification

data class NotificationBodyUiState(
    val id: Long,
    val otherName: String,
    val otherUid: Long,
    val conferenceId: Long,
    val conferenceName: String,
    val message: String,
    val profileImageUrl: String,
    val notificationDate: String = "23.08.03",
    val isAccepted: Boolean = false,
    val isRead: Boolean = false,
) {
    companion object {
        fun from(
            notification: Notification,
            notificationMember: NotificationMemberUiState?,
            conferenceName: String,
        ): NotificationBodyUiState = NotificationBodyUiState(
            id = notification.id,
            otherUid = notification.otherUid,
            otherName = notificationMember?.name ?: "",
            conferenceId = notification.eventId,
            conferenceName = conferenceName,
            message = notification.message,
            profileImageUrl = notificationMember?.profileImageUrl ?: "",
        )
    }
}
