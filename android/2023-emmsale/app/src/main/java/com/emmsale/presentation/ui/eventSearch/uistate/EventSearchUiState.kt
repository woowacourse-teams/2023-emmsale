package com.emmsale.presentation.ui.eventSearch.uistate

import com.emmsale.data.model.Event
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class EventSearchUiState(
    val events: List<Event>,
    override val fetchResult: FetchResult,
) : FetchResultUiState() {
    val eventSize: Int = events.size
}
