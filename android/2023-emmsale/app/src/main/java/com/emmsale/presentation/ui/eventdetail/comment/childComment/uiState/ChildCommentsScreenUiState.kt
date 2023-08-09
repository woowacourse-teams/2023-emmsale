package com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState

import com.emmsale.data.comment.Comment
import com.emmsale.data.member.Member

data class ChildCommentsScreenUiState(
    val isNotLogin: Boolean,
    val isLoading: Boolean,
    val isError: Boolean,
    val errorMessage: String,
    val parentComment: CommentUiState,
    val childComments: List<CommentUiState>,
) {
    companion object {
        val Loading = ChildCommentsScreenUiState(
            isNotLogin = false,
            isLoading = true,
            isError = false,
            errorMessage = "",
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

        fun create(comment: Comment, loginMember: Member) = ChildCommentsScreenUiState(
            isNotLogin = false,
            isLoading = false,
            isError = false,
            errorMessage = "",
            parentComment = CommentUiState.create(comment, loginMember),
            childComments = comment.childComments.map { CommentUiState.create(it, loginMember) },
        )
    }
}
