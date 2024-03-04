package com.emmsale.presentation.ui.notificationList.uiState

import com.emmsale.model.notification.Notification

data class NotificationsUiState(
    val recentNotifications: List<Notification> = emptyList(),
    val pastNotifications: List<Notification> = emptyList(),
) {
    constructor(notifications: List<Notification>) : this(
        recentNotifications = notifications.filter { !it.isRead }
            .sortedByDescending { it.createdAt },
        pastNotifications = notifications.filter { it.isRead }
            .sortedByDescending { it.createdAt },
    )

    fun deleteAllPastNotifications() = NotificationsUiState(
        recentNotifications = recentNotifications,
        pastNotifications = emptyList(),
    )

    fun readNotification(notificationId: Long): NotificationsUiState {
        val notification = recentNotifications.find { it.id == notificationId } ?: return this

        val recentNotifications = recentNotifications.filter { it.id != notificationId }
        val pastNotifications = (pastNotifications + notification.read())
            .sortedByDescending { it.createdAt }

        return NotificationsUiState(
            recentNotifications = recentNotifications,
            pastNotifications = pastNotifications,
        )
    }

    fun deleteNotification(notificationId: Long) = NotificationsUiState(
        recentNotifications = recentNotifications.filter { it.id != notificationId },
        pastNotifications = pastNotifications.filter { it.id != notificationId },
    )
}
