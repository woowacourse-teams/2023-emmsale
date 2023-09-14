package com.emmsale.presentation.ui.notificationConfig.uiState

import com.emmsale.data.model.EventTag

sealed interface NotificationTagsUiState {
    data class Success(val tags: List<EventTag>) : NotificationTagsUiState
    object Loading : NotificationTagsUiState
    object Error : NotificationTagsUiState
}
