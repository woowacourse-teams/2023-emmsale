package com.emmsale.presentation.ui.main.event.conference.uistate

data class ConferencesUiState(
    val conferences: List<ConferenceUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    val conferenceSize: Int = conferences.size
}
