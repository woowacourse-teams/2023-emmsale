package com.emmsale.presentation.ui.main.event.conferenceFilter.uistate

sealed class ConferenceFilterUiState {
    data class Success(
        val statuses: List<ConferenceFilterStatusUiState>,
        val tags: List<ConferenceFilterTagUiState>,
        var selectedStartDate: ConferenceFilterDateUiState,
        var selectedEndDate: ConferenceFilterDateUiState,
    ) : ConferenceFilterUiState()

    object Loading : ConferenceFilterUiState()
    object Error : ConferenceFilterUiState()
}
