package com.emmsale.presentation.ui.conferenceList.uiState

import com.emmsale.data.model.Event

data class ConferencesUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    val conferenceSize: Int = events.size
}
