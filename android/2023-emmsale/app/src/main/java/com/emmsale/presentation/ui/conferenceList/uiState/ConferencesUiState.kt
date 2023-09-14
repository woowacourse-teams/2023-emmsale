package com.emmsale.presentation.ui.conferenceList.uiState

data class ConferencesUiState(
    val conferences: List<ConferenceUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    val conferenceSize: Int = conferences.size
}
