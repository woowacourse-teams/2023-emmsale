package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate

import com.emmsale.data.notification.RecruitmentNotification
import com.emmsale.data.notification.RecruitmentNotificationStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class RecruitmentNotificationBodyUiState(
    val id: Long,
    val otherName: String,
    val otherUid: Long,
    val conferenceId: Long,
    val conferenceName: String,
    val message: String,
    val profileImageUrl: String,
    val notificationDate: String,
    val isAccepted: Boolean = false,
    val isRejected: Boolean = false,
    val isRead: Boolean = false,
) {
    fun changeToAcceptedState(): RecruitmentNotificationBodyUiState = copy(
        isAccepted = true,
        isRejected = false,
    )

    fun changeToRejectedState(): RecruitmentNotificationBodyUiState = copy(
        isAccepted = false,
        isRejected = true,
    )

    fun changeToReadState(): RecruitmentNotificationBodyUiState = copy(
        isRead = true,
    )

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
            notificationDate = recruitmentNotification.notificationDate.toUiString(),
            isAccepted = recruitmentNotification.status == RecruitmentNotificationStatus.ACCEPTED,
            isRejected = recruitmentNotification.status == RecruitmentNotificationStatus.REJECTED,
            isRead = recruitmentNotification.isRead,
        )

        private fun LocalDateTime.toUiString(): String =
            DateTimeFormatter.ofPattern("yyyy.MM.dd").format(this)
    }
}
