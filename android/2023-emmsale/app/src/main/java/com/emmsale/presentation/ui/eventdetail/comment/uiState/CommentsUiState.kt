package com.emmsale.presentation.ui.eventdetail.comment.uiState

import com.emmsale.data.comment.Comment

data class CommentsUiState(
    val isLoading: Boolean,
    val isFetchingError: Boolean,
    val isPostingError: Boolean,
    val isDeletionError: Boolean,
    val comments: List<CommentUiState>,
) {
    companion object {
        val Loading = CommentsUiState(
            isLoading = true,
            isFetchingError = false,
            isPostingError = false,
            isDeletionError = false,
            comments = listOf(),
        )

        fun create(
            comments: List<Comment>,
            loginMemberId: Long,
        ) = CommentsUiState(
            isLoading = false,
            isFetchingError = false,
            isPostingError = false,
            isDeletionError = false,
            comments = comments.map {
                CommentUiState.create(
                    comment = it,
                    loginMemberId = loginMemberId,
                )
            },
        )
    }
}
