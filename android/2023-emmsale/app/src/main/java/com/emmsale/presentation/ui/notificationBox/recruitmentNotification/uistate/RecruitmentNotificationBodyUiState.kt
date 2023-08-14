package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate

import com.emmsale.data.notification.recruitment.RecruitmentNotification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class RecruitmentNotificationBodyUiState(
    val id: Long,
    val senderName: String,
    val senderUid: Long,
    val eventId: Long,
    val eventName: String,
    val message: String,
    val profileImageUrl: String,
    val notificationDate: String,
    val recruitmentStatus: RecruitmentNotificationStatusUiState,
    val isRead: Boolean = false,
) {
    fun changeToAcceptedState(): RecruitmentNotificationBodyUiState = copy(
        recruitmentStatus = RecruitmentNotificationStatusUiState.ACCEPTED,
    )

    fun changeToRejectedState(): RecruitmentNotificationBodyUiState = copy(
        recruitmentStatus = RecruitmentNotificationStatusUiState.REJECTED,
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
            senderUid = recruitmentNotification.senderUid,
            senderName = notificationMember?.name ?: "",
            eventId = recruitmentNotification.eventId,
            eventName = conferenceName,
            message = recruitmentNotification.message,
            profileImageUrl = notificationMember?.profileImageUrl ?: "",
            notificationDate = recruitmentNotification.notificationDate.toUiString(),
            recruitmentStatus = RecruitmentNotificationStatusUiState.from(recruitmentNotification.status),
            isRead = recruitmentNotification.isRead,
        )

        private fun LocalDateTime.toUiString(): String =
            DateTimeFormatter.ofPattern("yyyy.MM.dd").format(this)
    }
}
