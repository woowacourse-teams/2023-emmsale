package com.emmsale.presentation.ui.onboarding.uistate

sealed class MemberSavingUiState {
    object None : MemberSavingUiState()
    object Success : MemberSavingUiState()
    object Failed : MemberSavingUiState()
}
