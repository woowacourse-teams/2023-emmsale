package com.emmsale.presentation.ui.postWriting.uiState

import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class PostUploadResultUiState(
    override val fetchResult: FetchResult,
    val responseId: Long? = null,
) : FetchResultUiState()
