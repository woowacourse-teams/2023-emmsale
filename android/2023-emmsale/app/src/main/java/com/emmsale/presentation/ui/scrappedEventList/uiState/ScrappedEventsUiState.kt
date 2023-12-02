package com.emmsale.presentation.ui.scrappedEventList.uiState

import com.emmsale.data.model.Event

data class ScrappedEventsUiState(
    val list: List<ScrappedEventUiState> = listOf(),
) {
    companion object {
        fun from(scrappedEvents: List<Event>): ScrappedEventsUiState =
            ScrappedEventsUiState(
                list = scrappedEvents.map { ScrappedEventUiState.from(it) },
            )
    }
}
