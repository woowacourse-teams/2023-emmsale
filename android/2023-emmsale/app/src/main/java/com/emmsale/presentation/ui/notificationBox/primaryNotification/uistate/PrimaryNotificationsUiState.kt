package com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate

data class PrimaryNotificationsUiState(
    val notifications: List<PrimaryNotificationUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isFetchingError: Boolean = false,
    val isDeleteNotificationError: Boolean = false,
) {
    fun deleteNotificationById(notificationId: Long): PrimaryNotificationsUiState =
        copy(notifications = notifications.filterNot { it.id == notificationId })

    val notificationIds = notifications.map(PrimaryNotificationUiState::id)

    companion object {
        val EMPTY: PrimaryNotificationsUiState = PrimaryNotificationsUiState()
    }
}
