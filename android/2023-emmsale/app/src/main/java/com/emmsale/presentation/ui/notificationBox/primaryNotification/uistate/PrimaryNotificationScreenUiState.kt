package com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate

import com.emmsale.data.notification.updated.UpdatedNotification

sealed interface PrimaryNotificationScreenUiState {
    data class Success(
        val recentNotifications: List<PrimaryNotificationUiState>,
        val pastNotifications: List<PrimaryNotificationUiState>,
    ) : PrimaryNotificationScreenUiState {
        companion object {
            fun create(
                recentNotifications: List<UpdatedNotification>,
                pastNotifications: List<UpdatedNotification>,
            ) = Success(
                recentNotifications = recentNotifications.map(PrimaryNotificationUiState::from),
                pastNotifications = pastNotifications.map(PrimaryNotificationUiState::from),
            )
        }
    }

    object Loading : PrimaryNotificationScreenUiState

    object Error : PrimaryNotificationScreenUiState
}
