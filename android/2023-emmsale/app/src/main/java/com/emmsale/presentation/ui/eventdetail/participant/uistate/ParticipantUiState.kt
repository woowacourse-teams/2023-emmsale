package com.emmsale.presentation.eventdetail.participant.uistate

import com.emmsale.data.participant.Participant

data class ParticipantUiState(
    val id: Long,
    val memberId: Long,
    val name: String,
    val imageUrl: String,
    val description: String?,
) {
    companion object {
        fun from(participant: Participant): ParticipantUiState = ParticipantUiState(
            id = participant.id,
            memberId = participant.memberId,
            name = participant.name,
            imageUrl = participant.imageUrl,
            description = participant.description,
        )
    }
}
