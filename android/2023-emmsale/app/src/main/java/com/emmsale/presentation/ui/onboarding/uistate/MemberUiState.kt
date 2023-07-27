package com.emmsale.presentation.ui.onboarding.uistate

sealed class MemberUiState {
    object Success : MemberUiState()
    object Loading : MemberUiState()
    object Failed : MemberUiState()
}
