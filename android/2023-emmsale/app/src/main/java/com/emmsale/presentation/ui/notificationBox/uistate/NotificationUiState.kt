package com.emmsale.presentation.ui.notificationBox.uistate

data class NotificationUiState(
    val id: Long,
    val otherName: String,
    val otherUid: Long,
    val conferenceId: Long,
    val conferenceName: String,
    val message: String,
)
