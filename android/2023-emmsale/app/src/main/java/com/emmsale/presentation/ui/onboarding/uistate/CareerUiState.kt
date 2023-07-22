package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.career.Career
import com.emmsale.data.career.CareerContent

data class CareerUiState(
    val category: String,
    val careerContents: List<CareerContentUiState>,
) {
    val careerContentsWithCategory: List<CareerContentUiState>
        get() = careerCategory().toList() + careerContents

    private fun careerCategory(): CareerContentUiState =
        CareerContentUiState(id = -1, name = category)

    companion object {
        fun from(tags: List<Career>): List<CareerUiState> =
            tags.map { from(it) }

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
    fun toList(): List<CareerContentUiState> = listOf(this)

    companion object {
        fun from(careerContents: List<CareerContent>): List<CareerContentUiState> =
            careerContents.map { from(it) }

        private fun from(careerContent: CareerContent): CareerContentUiState =
            CareerContentUiState(
                id = careerContent.id,
                name = careerContent.name
            )
    }
}
