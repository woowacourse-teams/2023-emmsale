package com.emmsale.presentation.ui.notificationTagConfig.uiState

import com.emmsale.model.EventTag

data class NotificationTagConfigUiState(
    val eventTag: EventTag,
    val isChecked: Boolean,
)
