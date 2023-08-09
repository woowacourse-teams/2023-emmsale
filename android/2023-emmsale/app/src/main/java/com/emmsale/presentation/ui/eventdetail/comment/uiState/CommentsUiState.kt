package com.emmsale.presentation.ui.eventdetail.comment.uiState

import com.emmsale.data.comment.Comment

data class CommentsUiState(
    val isNotLogin: Boolean,
    val isLoading: Boolean,
    val isCommentsFetchingError: Boolean,
    val isCommentPostingError: Boolean,
    val isCommentDeletionError: Boolean,
    val comments: List<CommentUiState>,
) {
    companion object {
        val Loading = CommentsUiState(
            isNotLogin = false,
            isLoading = true,
            isCommentsFetchingError = false,
            isCommentPostingError = false,
            isCommentDeletionError = false,
            comments = listOf(),
        )

        fun create(
            comments: List<Comment>,
            loginMemberId: Long,
        ) = CommentsUiState(
            isNotLogin = false,
            isLoading = false,
            isCommentsFetchingError = false,
            isCommentPostingError = false,
            isCommentDeletionError = false,
            comments = comments.map {
                CommentUiState.create(
                    comment = it,
                    loginMemberId = loginMemberId,
                )
            },
        )
    }
}
