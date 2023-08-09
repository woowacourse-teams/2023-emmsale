package com.emmsale.presentation.eventdetail.recruitment.uistate

sealed class RecruitmentStatusUiState {
    data class Success(val isParticipate: Boolean) : RecruitmentStatusUiState()
    object Error : RecruitmentStatusUiState()
}
