package com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate

import com.emmsale.data.notification.updated.UpdatedNotification

data class PrimaryNotificationsUiState(
    val notifications: List<PrimaryNotificationUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isFetchingError: Boolean = false,
) {
    companion object {
        fun from(updatedNotification: List<UpdatedNotification>): PrimaryNotificationsUiState =
            PrimaryNotificationsUiState(
                notifications = updatedNotification.map(PrimaryNotificationUiState::from),
            )
    }
}
