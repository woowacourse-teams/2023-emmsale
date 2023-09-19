package com.emmsale.presentation.ui.onboarding.uiState

sealed class MemberSavingUiState {
    object None : MemberSavingUiState()
    object Success : MemberSavingUiState()
    object Failed : MemberSavingUiState()
}
