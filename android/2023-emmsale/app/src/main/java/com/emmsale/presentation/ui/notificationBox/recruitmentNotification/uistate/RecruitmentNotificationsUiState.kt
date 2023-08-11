package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate

data class RecruitmentNotificationsUiState(
    val notifications: List<RecruitmentNotificationHeaderUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    fun toggleNotificationExpanded(eventId: Long): RecruitmentNotificationsUiState =
        copy(notifications = toggleExpanded(eventId))

    fun changeAcceptStateBy(notificationId: Long): RecruitmentNotificationsUiState =
        copy(notifications = notifications.map { it.changeToAcceptedStateBy(notificationId) })

    fun changeRejectStateBy(notificationId: Long): RecruitmentNotificationsUiState =
        copy(notifications = notifications.map { it.changeToRejectedStateBy(notificationId) })

    private fun toggleExpanded(eventId: Long): List<RecruitmentNotificationHeaderUiState> {
        return notifications.map { header ->
            if (header.eventId == eventId) {
                header.toggleExpanded()
            } else {
                header
            }
        }
    }
}
