package com.emmsale.presentation.ui.competitionList.uiState

import com.emmsale.data.model.Competition

data class CompetitionUiState(
    val competition: Competition,
) {
    companion object {
        fun from(competition: Competition): CompetitionUiState {
            return CompetitionUiState(competition)
        }
    }
}
