package com.emmsale.presentation.ui.scrappedEventList.uiState

import com.emmsale.data.model.ScrappedEvent

data class ScrappedEventsUiState(
    val list: List<ScrappedEventUiState> = listOf(),
    val isError: Boolean = false,
    val isLoading: Boolean = false,
) {
    companion object {
        fun from(scrappedEvents: List<ScrappedEvent>): ScrappedEventsUiState =
            ScrappedEventsUiState(
                list = scrappedEvents.map { ScrappedEventUiState.from(it) },
                isError = false,
                isLoading = false,
            )
    }
}
