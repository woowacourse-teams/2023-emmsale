package com.emmsale.presentation.ui.comment.uiState

import com.emmsale.data.comment.Comment
import com.emmsale.data.member.Member1

data class CommentsScreenUiState(
    val isNotLogin: Boolean = false,
    val isLoading: Boolean,
    val isError: Boolean,
    val errorMessage: String,
    val comments: List<CommentUiState>,
) {
    companion object {
        val Loading = CommentsScreenUiState(
            isLoading = true,
            isError = false,
            errorMessage = "",
            comments = listOf()
        )

        fun create(
            comments: List<Comment>,
            loginMember: Member1,
        ) = CommentsScreenUiState(
            isLoading = false,
            isError = false,
            errorMessage = "",
            comments = comments.map {
                CommentUiState.create(
                    comment = it,
                    loginMember = loginMember,
                )
            }
        )
    }
}
