package com.emmsale.presentation.ui.login.uistate

sealed class LoginUiState {
    object Login : LoginUiState()
    object Onboarded : LoginUiState()
    object Loading : LoginUiState()
    object Error : LoginUiState()
}
