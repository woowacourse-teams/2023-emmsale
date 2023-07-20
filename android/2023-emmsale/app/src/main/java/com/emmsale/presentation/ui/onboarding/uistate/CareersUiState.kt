package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.career.Career
import com.emmsale.presentation.ui.onboarding.CareerCategory

sealed class CareersUiState {
    data class Success(val careers: List<CareerUiState>) : CareersUiState() {
        fun findCareer(category: CareerCategory): CareerUiState? =
            careers.find { it.category == category.title }
    }

    object Error : CareersUiState()

    companion object {
        fun from(careersResult: List<Career>): Success =
            Success(careers = CareerUiState.from(careersResult))
    }
}
