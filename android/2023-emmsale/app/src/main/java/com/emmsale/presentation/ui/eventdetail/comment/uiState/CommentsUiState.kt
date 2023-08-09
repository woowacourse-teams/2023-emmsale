package com.emmsale.presentation.ui.eventdetail.comment.uiState

import com.emmsale.data.comment.Comment
import com.emmsale.data.member.Member

data class CommentsUiState(
    val isNotLogin: Boolean,
    val isLoading: Boolean,
    val isCommentsFetchingError: Boolean,
    val isCommentPostingError: Boolean,
    val isCommentDeletionError: Boolean,
    val errorMessage: String,
    val comments: List<CommentUiState>,
) {
    companion object {
        val Loading = CommentsUiState(
            isNotLogin = false,
            isLoading = true,
            isCommentsFetchingError = false,
            isCommentPostingError = false,
            isCommentDeletionError = false,
            errorMessage = "",
            comments = listOf(),
        )

        fun create(
            comments: List<Comment>,
            loginMember: Member,
        ) = CommentsUiState(
            isNotLogin = false,
            isLoading = false,
            isCommentsFetchingError = false,
            isCommentPostingError = false,
            isCommentDeletionError = false,
            errorMessage = "",
            comments = comments.map {
                CommentUiState.create(
                    comment = it,
                    loginMember = loginMember,
                )
            },
        )
    }
}
