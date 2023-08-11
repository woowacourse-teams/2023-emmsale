package com.emmsale.presentation.ui.eventdetail.recruitment.uistate

import com.emmsale.data.recruitment.Recruitment

data class RecruitmentsUiState(
    val list: List<RecruitmentUiState> = listOf(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    companion object {
        fun from(recruitments: List<Recruitment>): RecruitmentsUiState {
            return RecruitmentsUiState(
                list = recruitments.map { RecruitmentUiState.from(it) },
                isLoading = false,
                isError = false,
            )
        }
    }
}
