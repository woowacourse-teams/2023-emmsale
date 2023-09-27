package com.emmsale.presentation.ui.generalPostList.uiState

import com.emmsale.data.model.GeneralPost
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class PostsUiState(
    override val fetchResult: FetchResult = FetchResult.LOADING,
    val generalPosts: List<GeneralPost> = emptyList(),
) : FetchResultUiState()
