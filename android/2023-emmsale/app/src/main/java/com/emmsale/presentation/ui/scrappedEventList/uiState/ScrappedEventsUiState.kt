package com.emmsale.presentation.ui.scrappedEventList.uiState

import com.emmsale.data.model.ScrappedEvent
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class ScrappedEventsUiState(
    val list: List<ScrappedEventUiState> = listOf(),
    override val fetchResult: FetchResult = FetchResult.LOADING,
) : FetchResultUiState() {
    companion object {
        fun from(scrappedEvents: List<ScrappedEvent>): ScrappedEventsUiState =
            ScrappedEventsUiState(
                list = scrappedEvents.map { ScrappedEventUiState.from(it) },
                fetchResult = FetchResult.SUCCESS,
            )
    }
}
