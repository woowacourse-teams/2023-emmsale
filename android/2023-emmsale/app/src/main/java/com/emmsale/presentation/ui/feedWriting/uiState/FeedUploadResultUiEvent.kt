package com.emmsale.presentation.ui.feedWriting.uiState

import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class FeedUploadResultUiEvent(
    override val fetchResult: FetchResult,
    val responseId: Long? = null,
) : FetchResultUiState()
