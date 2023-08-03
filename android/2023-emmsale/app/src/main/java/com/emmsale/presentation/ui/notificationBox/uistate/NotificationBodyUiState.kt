package com.emmsale.presentation.ui.notificationBox.uistate

data class NotificationBodyUiState(
    val id: Long,
    val otherName: String,
    val otherUid: Long,
    val conferenceId: Long,
    val conferenceName: String,
    val message: String,
    val profileImageUrl: String,
    val notificationDate: String = "23.08.03",
)
