package com.emmsale.presentation.ui.login.uiState

sealed class LoginUiState {
    object Login : LoginUiState()
    object Onboarded : LoginUiState()
    object Loading : LoginUiState()
    object Error : LoginUiState()
}
