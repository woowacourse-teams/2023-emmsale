package com.emmsale.presentation.ui.childCommentList.uiState

import com.emmsale.data.model.Comment
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState

data class ChildCommentsUiState(
    override val fetchResult: FetchResult,
    val comments: List<CommentUiState>,
) : FetchResultUiState() {
    companion object {
        val Loading: ChildCommentsUiState = ChildCommentsUiState(
            fetchResult = FetchResult.LOADING,
            comments = emptyList(),
        )

        fun create(
            uid: Long,
            parentComment: Comment,
        ) = ChildCommentsUiState(
            fetchResult = FetchResult.SUCCESS,
            comments = listOf(CommentUiState.create(uid, parentComment)) +
                parentComment.childComments.map { CommentUiState.create(uid, it) },
        )
    }
}
