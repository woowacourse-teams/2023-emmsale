package com.emmsale.presentation.ui.notificationBox.uistate

data class NotificationHeaderUiState(
    val eventId: Long,
    val conferenceName: String,
    var isExpanded: Boolean = false,
    val notifications: List<NotificationBodyUiState>,
)
