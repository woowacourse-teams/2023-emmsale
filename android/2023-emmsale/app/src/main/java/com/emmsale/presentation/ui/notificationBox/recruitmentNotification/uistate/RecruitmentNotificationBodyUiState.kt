package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate

import com.emmsale.data.notification.RecruitmentNotification

data class RecruitmentNotificationBodyUiState(
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
            recruitmentNotification: RecruitmentNotification,
            notificationMember: RecruitmentNotificationMemberUiState?,
            conferenceName: String,
        ): RecruitmentNotificationBodyUiState = RecruitmentNotificationBodyUiState(
            id = recruitmentNotification.id,
            otherUid = recruitmentNotification.otherUid,
            otherName = notificationMember?.name ?: "",
            conferenceId = recruitmentNotification.eventId,
            conferenceName = conferenceName,
            message = recruitmentNotification.message,
            profileImageUrl = notificationMember?.profileImageUrl ?: "",
        )
    }
}
