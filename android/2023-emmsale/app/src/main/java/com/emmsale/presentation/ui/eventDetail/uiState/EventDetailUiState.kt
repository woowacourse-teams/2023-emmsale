package com.emmsale.presentation.ui.eventDetail.uiState

import com.emmsale.data.model.EventDetail
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class EventDetailUiState(
    override val fetchResult: FetchResult = FetchResult.SUCCESS,
    val eventDetail: EventDetail? = null,
) : FetchResultUiState()
