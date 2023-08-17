package com.emmsale.presentation.ui.main.event.competition.uistate

data class CompetitionsUiState(
    val competitions: List<CompetitionUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    val competitionSize: Int = competitions.size
}
