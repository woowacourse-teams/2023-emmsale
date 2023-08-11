package com.emmsale.presentation.ui.eventdetail.recruitment.uistate

import com.emmsale.data.recruitment.Recruitment

sealed class RecruitmentsUiState {
    data class Success(
        val value: List<RecruitmentUiState>,
    ) : RecruitmentsUiState()

    object Error : RecruitmentsUiState()

    companion object {
        fun from(recruitments: List<Recruitment>): Success =
            Success(recruitments.map(RecruitmentUiState::from))
    }
}
