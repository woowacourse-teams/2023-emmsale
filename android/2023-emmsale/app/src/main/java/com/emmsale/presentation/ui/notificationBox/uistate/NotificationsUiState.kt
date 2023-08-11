package com.emmsale.presentation.ui.notificationBox.uistate

data class NotificationsUiState(
    val notifications: List<NotificationHeaderUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    fun toggleNotificationExpanded(eventId: Long): NotificationsUiState =
        copy(notifications = toggleExpanded(eventId))

    fun deleteNotification(notificationId: Long): NotificationsUiState =
        copy(notifications = notifications.map { it.deleteNotification(notificationId) })

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
