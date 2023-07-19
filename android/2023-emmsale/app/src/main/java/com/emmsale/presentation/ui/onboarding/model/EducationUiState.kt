package com.emmsale.presentation.ui.onboarding.model

data class EducationUiState(
    val name: String,
) {
    operator fun plus(educations: List<EducationUiState>): List<EducationUiState> =
        listOf(this) + educations

    companion object {
        fun from(dataEducations: List<com.emmsale.data.education.Education>): List<EducationUiState> =
            dataEducations.map { from(it) }

        private fun from(dataEducation: com.emmsale.data.education.Education): EducationUiState =
            EducationUiState(name = dataEducation.name)

        fun empty(): EducationUiState = EducationUiState(name = "-")
    }
}
