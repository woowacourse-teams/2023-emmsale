package com.emmsale.presentation.ui.notificationTagConfig.uiState

sealed interface NotificationTagConfigUiEvent {
    object UpdateComplete : NotificationTagConfigUiEvent
    object UpdateFail : NotificationTagConfigUiEvent
}
