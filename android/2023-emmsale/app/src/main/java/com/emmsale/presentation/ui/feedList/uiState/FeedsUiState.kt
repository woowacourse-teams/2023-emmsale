package com.emmsale.presentation.ui.feedList.uiState

import com.emmsale.data.model.Feed
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class FeedsUiState(
    override val fetchResult: FetchResult = FetchResult.LOADING,
    val feeds: List<Feed> = emptyList(),
) : FetchResultUiState()
