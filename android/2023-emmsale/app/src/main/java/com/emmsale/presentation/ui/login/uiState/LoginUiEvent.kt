package com.emmsale.presentation.ui.login.uiState

sealed interface LoginUiEvent {
    object LoginComplete : LoginUiEvent
    object LoginFail : LoginUiEvent
    object JoinComplete : LoginUiEvent
}
