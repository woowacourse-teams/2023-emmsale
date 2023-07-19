package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.education.Education
import com.emmsale.presentation.ui.onboarding.model.EducationUiState

sealed class EducationsUiState {
    data class Success(val educations: List<EducationUiState>) : EducationsUiState() {
        fun insertFront(education: EducationUiState): Success =
            Success(education + this.educations)
    }

    object Error : EducationsUiState()

    companion object {
        fun from(education: ApiSuccess<List<Education>>): Success =
            Success(educations = EducationUiState.from(education.data))
    }
}
