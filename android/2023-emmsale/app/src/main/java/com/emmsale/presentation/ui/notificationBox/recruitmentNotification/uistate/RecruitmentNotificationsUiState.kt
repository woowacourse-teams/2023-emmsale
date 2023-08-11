package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate

data class RecruitmentNotificationsUiState(
    val notifications: List<RecruitmentNotificationHeaderUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    fun toggleNotificationExpanded(eventId: Long): RecruitmentNotificationsUiState =
        copy(notifications = toggleExpanded(eventId))

    fun deleteNotification(notificationId: Long): RecruitmentNotificationsUiState =
        copy(notifications = notifications.map { it.deleteNotification(notificationId) })

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
