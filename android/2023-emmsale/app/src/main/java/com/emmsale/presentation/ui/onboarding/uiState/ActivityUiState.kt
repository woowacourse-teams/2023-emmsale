package com.emmsale.presentation.ui.onboarding.uiState

import com.emmsale.model.Activity

data class ActivityUiState(
    val activity: Activity,
    val isSelected: Boolean = false,
)
