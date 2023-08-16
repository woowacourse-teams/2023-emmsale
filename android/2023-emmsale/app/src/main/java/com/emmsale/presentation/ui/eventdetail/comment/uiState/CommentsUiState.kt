package com.emmsale.presentation.ui.eventdetail.comment.uiState

import com.emmsale.data.comment.Comment

data class CommentsUiState(
    val isLoading: Boolean,
    val isFetchingError: Boolean,
    val isPostingError: Boolean,
    val isUpdateError: Boolean,
    val isDeletionError: Boolean,
    val comments: List<CommentUiState>,
) {

    fun changeToLoadingState(): CommentsUiState = copy(
        isLoading = true,
        isFetchingError = false,
        isPostingError = false,
        isUpdateError = false,
        isDeletionError = false,
    )

    fun changeToFetchingErrorState(): CommentsUiState = copy(
        isLoading = false,
        isFetchingError = true,
        isPostingError = false,
        isUpdateError = false,
        isDeletionError = false,
    )

    fun changeToPostingErrorState(): CommentsUiState = copy(
        isLoading = false,
        isFetchingError = false,
        isPostingError = true,
        isUpdateError = false,
        isDeletionError = false,
    )

    fun changeToUpdateErrorState(): CommentsUiState = copy(
        isLoading = false,
        isFetchingError = false,
        isPostingError = false,
        isUpdateError = true,
        isDeletionError = false,
    )

    fun changeToDeleteErrorState(): CommentsUiState = copy(
        isLoading = false,
        isFetchingError = false,
        isPostingError = false,
        isUpdateError = false,
        isDeletionError = true,
    )

    fun changeCommentsState(comments: List<Comment>, loginMemberId: Long): CommentsUiState = copy(
        isLoading = false,
        isFetchingError = false,
        isPostingError = false,
        isDeletionError = false,
        comments = comments.map {
            CommentUiState.create(comment = it, loginMemberId = loginMemberId)
        },
    )

    companion object {
        val Loading = CommentsUiState(
            isLoading = true,
            isFetchingError = false,
            isPostingError = false,
            isUpdateError = false,
            isDeletionError = false,
            comments = listOf(),
        )
    }
}
