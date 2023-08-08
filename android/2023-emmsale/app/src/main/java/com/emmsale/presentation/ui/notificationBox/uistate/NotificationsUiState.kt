package com.emmsale.presentation.ui.notificationBox.uistate

data class NotificationsUiState(
    val notifications: List<NotificationHeaderUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    fun toggleNotificationExpanded(eventId: Long): NotificationsUiState =
        copy(notifications = toggleExpanded(eventId))

    private fun toggleExpanded(eventId: Long): List<NotificationHeaderUiState> {
        return notifications.map { header ->
            if (header.eventId == eventId) {
                header.toggleExpanded()
            } else {
                header
            }
        }
    }
}
