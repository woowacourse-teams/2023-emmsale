package com.emmsale.presentation.ui.competitionList.uiState

import com.emmsale.data.model.Competition
import com.emmsale.data.model.Status

data class CompetitionUiState(
    val competition: Competition,
) {
    fun getStatus(): String = when (competition.status) {
        Status.IN_PROGRESS -> "진행중"
        Status.UPCOMING -> "D-${competition.dDay}"
        Status.ENDED -> "마감"
    }

    companion object {
        fun from(competition: Competition): CompetitionUiState = CompetitionUiState(
            competition,
        )
    }
}
