package com.emmsale.presentation.ui.notificationBox.uistate

data class NotificationsUiState(
    val notifications: List<NotificationUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)
