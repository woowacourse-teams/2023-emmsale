package com.emmsale.presentation.ui.competitionList.uiState

import com.emmsale.data.model.Competition

data class CompetitionUiState(
    val competition: Competition,
) {
    fun getStatus(): String = when (competition.status) {
        Competition.Status.IN_PROGRESS -> "진행중"
        Competition.Status.UPCOMING -> "D-${competition.dDay}"
        Competition.Status.ENDED -> "마감"
    }

    companion object {
        fun from(competition: Competition): CompetitionUiState = CompetitionUiState(
            competition,
        )
    }
}
