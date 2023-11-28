package com.emmsale.presentation.ui.eventDetail.uiState

import com.emmsale.data.model.Event
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class EventDetailUiState(
    override val fetchResult: FetchResult = FetchResult.SUCCESS,
    val eventDetail: Event? = null,
) : FetchResultUiState()
