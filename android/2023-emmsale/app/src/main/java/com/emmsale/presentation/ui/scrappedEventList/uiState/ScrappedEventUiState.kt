package com.emmsale.presentation.ui.scrappedEventList.uiState

import com.emmsale.data.model.ScrappedEvent

data class ScrappedEventUiState(
    val scrappedEvent: ScrappedEvent,
) {
    companion object {
        fun from(scrappedEvent: ScrappedEvent) = ScrappedEventUiState(
            scrappedEvent = scrappedEvent,
        )
    }
}
