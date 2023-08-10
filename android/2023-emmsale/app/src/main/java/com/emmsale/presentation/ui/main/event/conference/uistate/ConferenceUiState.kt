package com.emmsale.presentation.ui.main.event.conference.uistate

data class ConferenceUiState(
    val conferenceItems: List<ConferenceItemUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    val conferenceSize: Int = conferenceItems.size
}
