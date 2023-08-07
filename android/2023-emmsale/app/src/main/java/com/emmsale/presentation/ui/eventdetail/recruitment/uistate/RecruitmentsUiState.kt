package com.emmsale.presentation.ui.eventdetail.recruitment.uistate

import com.emmsale.data.participant.Participant

sealed class RecruitmentsUiState {
    data class Success(
        val value: List<RecruitmentUiState>,
    ) : RecruitmentsUiState()

    object Error : RecruitmentsUiState()

    companion object {
        fun from(participants: List<Participant>): Success =
            Success(participants.map(RecruitmentUiState.Companion::from))
    }
}
