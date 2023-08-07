package com.emmsale.presentation.ui.eventdetail.recruitment.uistate

import com.emmsale.data.participant.Participant

data class RecruitmentUiState(
    val id: Long,
    val memberId: Long,
    val name: String,
    val imageUrl: String,
    val description: String?,
) {
    companion object {
        fun from(participant: Participant): RecruitmentUiState = RecruitmentUiState(
            id = participant.id,
            memberId = participant.memberId,
            name = participant.name,
            imageUrl = participant.imageUrl,
            description = participant.description,
        )
    }
}
