package com.emmsale.presentation.ui.eventdetail.comment.uiState

import com.emmsale.data.comment.Comment

data class CommentsUiState(
    val isLoading: Boolean,
    val isError: Boolean,
    val comments: List<CommentUiState>,
) {

    fun changeToLoadingState(): CommentsUiState = copy(
        isLoading = true,
        isError = false,
    )

    fun changeToFetchingErrorState(): CommentsUiState = copy(
        isLoading = false,
        isError = true,
    )

    fun changeCommentsState(comments: List<Comment>, loginMemberId: Long): CommentsUiState = copy(
        isLoading = false,
        isError = false,
        comments = comments.map {
            CommentUiState.create(comment = it, loginMemberId = loginMemberId)
        },
    )

    companion object {
        val Loading = CommentsUiState(
            isLoading = true,
            isError = false,
            comments = listOf(),
        )
    }
}
