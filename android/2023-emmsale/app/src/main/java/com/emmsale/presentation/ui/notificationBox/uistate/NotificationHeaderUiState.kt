package com.emmsale.presentation.ui.notificationBox.uistate

data class NotificationHeaderUiState(
    val eventId: Long,
    val conferenceName: String,
    val isExpanded: Boolean = false,
    val notifications: List<NotificationBodyUiState>,
) {
    fun toggleExpanded(): NotificationHeaderUiState = copy(isExpanded = !isExpanded)

    fun deleteNotification(notificationId: Long): NotificationHeaderUiState =
        copy(notifications = notifications.filterNot { it.id != notificationId })
}
