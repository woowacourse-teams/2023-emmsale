package com.emmsale.presentation.ui.postList.uiState

import com.emmsale.data.model.Post
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class PostsUiState(
    override val fetchResult: FetchResult = FetchResult.LOADING,
    val posts: List<Post> = emptyList(),
) : FetchResultUiState()
