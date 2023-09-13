package com.emmsale.presentation.ui.main.event.competition.uistate

import com.emmsale.data.model.CompetitionStatus
import com.emmsale.data.model.EventTag

data class CompetitionSelectedFilteringOptionUiState(
    val id: Long,
    val name: String,
) {
    companion object {
        fun from(competitionStatus: CompetitionStatus): CompetitionSelectedFilteringOptionUiState =
            CompetitionSelectedFilteringOptionUiState(
                id = competitionStatus.id,
                name = competitionStatus.toText(),
            )

        private fun CompetitionStatus.toText(): String = when (this) {
            CompetitionStatus.IN_PROGRESS -> "진행 중"
            CompetitionStatus.SCHEDULED -> "진행 예정"
            CompetitionStatus.ENDED -> "마감"
        }

        fun from(eventTag: EventTag): CompetitionSelectedFilteringOptionUiState =
            CompetitionSelectedFilteringOptionUiState(
                id = eventTag.id,
                name = eventTag.name,
            )
    }
}
