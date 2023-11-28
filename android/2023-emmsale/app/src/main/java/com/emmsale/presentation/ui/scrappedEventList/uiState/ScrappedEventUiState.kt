package com.emmsale.presentation.ui.scrappedEventList.uiState

import com.emmsale.data.model.Event

data class ScrappedEventUiState(
    val event: Event,
) {
    companion object {
        fun from(scrappedEvent: Event) = ScrappedEventUiState(
            event = scrappedEvent,
        )
    }
}
