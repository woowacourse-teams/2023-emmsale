package com.emmsale.presentation.ui.splash.uistate

sealed class SplashUiState {
    data class Done(
        val isAutoLogin: Boolean = false,
    ) : SplashUiState()

    data class Loading(
        val splashTimeMs: Long = 1500L,
    ) : SplashUiState()
}
