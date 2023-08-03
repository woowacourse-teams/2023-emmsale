package com.emmsale.presentation.eventdetail.participant.uistate

sealed class ParticipationStatusUiState {
    data class Success(val isParticipate: Boolean) : ParticipationStatusUiState()
    object Error : ParticipationStatusUiState()
}
