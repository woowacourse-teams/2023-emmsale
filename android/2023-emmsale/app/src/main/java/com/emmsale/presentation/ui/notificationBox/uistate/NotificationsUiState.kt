package com.emmsale.presentation.ui.notificationBox.uistate

data class NotificationsUiState(
    val notifications: List<NotificationHeaderUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)
