package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate

data class RecruitmentNotificationHeaderUiState(
    val eventId: Long,
    val conferenceName: String,
    val isExpanded: Boolean = false,
    val notifications: List<RecruitmentNotificationBodyUiState>,
    val isRead: Boolean = false,
) {
    fun toggleExpanded(): RecruitmentNotificationHeaderUiState = copy(isExpanded = !isExpanded)

    fun changeToAcceptedStateBy(notificationId: Long): RecruitmentNotificationHeaderUiState =
        copy(
            notifications = notifications.map {
                if (it.id == notificationId) it.changeToAcceptedState() else it
            },
        )

    fun changeToRejectedStateBy(notificationId: Long): RecruitmentNotificationHeaderUiState =
        copy(
            notifications = notifications.map {
                if (it.id == notificationId) it.changeToRejectedState() else it
            },
        )

    fun changeToReadState(): RecruitmentNotificationHeaderUiState = copy(
        isRead = true,
        notifications = notifications.map { it.changeToReadState() },
    )
}
