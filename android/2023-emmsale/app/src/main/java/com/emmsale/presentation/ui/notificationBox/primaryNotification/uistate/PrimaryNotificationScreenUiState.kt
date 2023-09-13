package com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate

import com.emmsale.data.model.updatedNotification.UpdatedNotification

sealed interface PrimaryNotificationScreenUiState {
    data class Success(
        val recentNotifications: List<PrimaryNotificationUiState>,
        val pastNotifications: List<PrimaryNotificationUiState>,
    ) : PrimaryNotificationScreenUiState {
        companion object {
            fun from(notifications: List<UpdatedNotification>) = Success(
                recentNotifications = notifications.filterNot { it.isRead }
                    .map(PrimaryNotificationUiState::from),
                pastNotifications = notifications.filter { it.isRead }
                    .map(PrimaryNotificationUiState::from),
            )
        }
    }

    object Loading : PrimaryNotificationScreenUiState

    object Error : PrimaryNotificationScreenUiState
}
