package com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState

import com.emmsale.data.comment.Comment

data class ChildCommentsUiState(
    val isLoading: Boolean,
    val isFetchingError: Boolean,
    val isPostingError: Boolean,
    val isDeletionError: Boolean,
    val parentComment: CommentUiState,
    val childComments: List<CommentUiState>,
) {
    companion object {
        val Loading = ChildCommentsUiState(
            isLoading = true,
            isFetchingError = false,
            isPostingError = false,
            isDeletionError = false,
            parentComment = CommentUiState(
                authorId = -1,
                authorName = "",
                lastModifiedDate = "",
                isUpdated = false,
                commentId = -1,
                content = "",
                isUpdatable = false,
                isDeletable = false,
                isDeleted = false,
            ),
            childComments = listOf(),
        )

        fun create(comment: Comment, loginMemberId: Long) = ChildCommentsUiState(
            isLoading = false,
            isFetchingError = false,
            isPostingError = false,
            isDeletionError = false,
            parentComment = CommentUiState.create(comment, loginMemberId),
            childComments = comment.childComments.map { CommentUiState.create(it, loginMemberId) },
        )
    }
}
