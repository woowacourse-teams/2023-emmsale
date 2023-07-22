package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.career.Career
import com.emmsale.data.career.CareerContent

data class CareerUiState(
    val category: String,
    val careerContents: List<CareerContentUiState>,
) {
    companion object {
        fun from(tags: List<Career>): List<CareerUiState> = tags.map(::from)

        private fun from(career: Career): CareerUiState = CareerUiState(
            category = career.category,
            careerContents = CareerContentUiState.from(career.careerContents),
        )
    }
}

data class CareerContentUiState(
    val id: Int,
    val name: String,
    var isSelected: Boolean = false,
) {
    companion object {
        fun from(careerContents: List<CareerContent>): List<CareerContentUiState> =
            careerContents.map(::from)

        private fun from(careerContent: CareerContent): CareerContentUiState =
            CareerContentUiState(
                id = careerContent.id,
                name = careerContent.name
            )
    }
}
