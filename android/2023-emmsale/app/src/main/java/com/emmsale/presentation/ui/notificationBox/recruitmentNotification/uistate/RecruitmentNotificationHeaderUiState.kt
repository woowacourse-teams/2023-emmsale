package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate

data class RecruitmentNotificationHeaderUiState(
    val eventId: Long,
    val conferenceName: String,
    val isExpanded: Boolean = false,
    val notifications: List<RecruitmentNotificationBodyUiState>,
) {
    fun toggleExpanded(): RecruitmentNotificationHeaderUiState = copy(isExpanded = !isExpanded)

    fun deleteNotification(notificationId: Long): RecruitmentNotificationHeaderUiState =
        copy(notifications = notifications.filterNot { it.id != notificationId })
}
