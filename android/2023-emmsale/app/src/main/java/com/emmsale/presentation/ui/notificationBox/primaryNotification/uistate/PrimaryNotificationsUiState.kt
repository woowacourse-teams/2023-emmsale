package com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate

import com.emmsale.data.notification.updated.UpdatedNotification

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

        fun from(updatedNotification: List<UpdatedNotification>): PrimaryNotificationsUiState =
            PrimaryNotificationsUiState(
                notifications = updatedNotification.map(PrimaryNotificationUiState::from),
            )
    }
}
