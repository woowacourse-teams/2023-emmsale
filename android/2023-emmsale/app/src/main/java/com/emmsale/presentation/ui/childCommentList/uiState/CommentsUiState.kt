package com.emmsale.presentation.ui.childCommentList.uiState

import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState

data class CommentsUiState(
    override val fetchResult: FetchResult,
    val comments: List<CommentUiState>,
) : FetchResultUiState() {
    companion object {
        val Loading: CommentsUiState = CommentsUiState(
            fetchResult = FetchResult.LOADING,
            comments = emptyList(),
        )
    }
}
