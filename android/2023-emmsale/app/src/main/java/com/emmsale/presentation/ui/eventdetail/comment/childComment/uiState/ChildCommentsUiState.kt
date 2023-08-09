package com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState

import com.emmsale.data.comment.Comment

data class ChildCommentsUiState(
    val isNotLogin: Boolean,
    val isLoading: Boolean,
    val isCommentsFetchingError: Boolean,
    val isCommentPostingError: Boolean,
    val isCommentDeletionError: Boolean,
    val parentComment: CommentUiState,
    val childComments: List<CommentUiState>,
) {
    companion object {
        val Loading = ChildCommentsUiState(
            isNotLogin = false,
            isLoading = true,
            isCommentsFetchingError = false,
            isCommentPostingError = false,
            isCommentDeletionError = false,
            parentComment = CommentUiState(
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
            isNotLogin = false,
            isLoading = false,
            isCommentsFetchingError = false,
            isCommentPostingError = false,
            isCommentDeletionError = false,
            parentComment = CommentUiState.create(comment, loginMemberId),
            childComments = comment.childComments.map { CommentUiState.create(it, loginMemberId) },
        )
    }
}
