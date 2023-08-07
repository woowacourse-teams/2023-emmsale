package com.emmsale.presentation.ui.eventdetail.recruitment.uistate

sealed class RecruitmentStatusUiState {
    data class Success(val isParticipate: Boolean) : RecruitmentStatusUiState()
    object Error : RecruitmentStatusUiState()
}
