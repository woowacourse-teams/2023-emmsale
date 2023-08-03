package com.emmsale.presentation.eventdetail.participant.uistate

import com.emmsale.data.participant.Participant

sealed class ParticipantsUiState {
    data class Success(
        val value: List<ParticipantUiState>,
    ) : ParticipantsUiState()

    object Error : ParticipantsUiState()

    companion object {
        fun from(participants: List<Participant>): ParticipantsUiState.Success =
            ParticipantsUiState.Success(participants.map(ParticipantUiState::from))
    }
}
