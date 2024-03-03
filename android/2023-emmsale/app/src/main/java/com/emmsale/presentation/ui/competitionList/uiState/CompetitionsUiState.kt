package com.emmsale.presentation.ui.competitionList.uiState

import com.emmsale.model.Event

data class CompetitionsUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    val competitionSize: Int = events.size
}
