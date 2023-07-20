package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.career.Career
import com.emmsale.data.common.ApiSuccess

sealed class CareersUiState {
    data class Success(val careers: List<CareerUiState>) : CareersUiState()
    object Error : CareersUiState()

    companion object {
        fun from(careersResult: List<Career>): Success =
            Success(careers = CareerUiState.from(careersResult))
    }
}
