package com.emmsale.presentation.ui.competitionFilter.uiState

import com.emmsale.model.CompetitionStatus
import com.emmsale.model.EventTag

data class CompetitionFilteringOptionUiState(
    val id: Long,
    val name: String,
    val isSelected: Boolean = false,
) {
    fun toggleSelection(): CompetitionFilteringOptionUiState = copy(isSelected = !isSelected)

    companion object {
        fun from(competitionStatus: CompetitionStatus): CompetitionFilteringOptionUiState =
            CompetitionFilteringOptionUiState(
                id = competitionStatus.id,
                name = competitionStatus.toText(),
            )

        private fun CompetitionStatus.toText(): String = when (this) {
            CompetitionStatus.IN_PROGRESS -> "진행 중"
            CompetitionStatus.SCHEDULED -> "진행 예정"
            CompetitionStatus.ENDED -> "마감"
        }

        fun from(eventTag: EventTag): CompetitionFilteringOptionUiState =
            CompetitionFilteringOptionUiState(
                id = eventTag.id,
                name = eventTag.name,
            )
    }
}
