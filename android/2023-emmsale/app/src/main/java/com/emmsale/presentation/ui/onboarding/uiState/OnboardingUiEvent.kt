package com.emmsale.presentation.ui.onboarding.uiState

sealed interface OnboardingUiEvent {
    object FieldLimitExceedChecked : OnboardingUiEvent
    object JoinComplete : OnboardingUiEvent
    object JoinFail : OnboardingUiEvent
}
