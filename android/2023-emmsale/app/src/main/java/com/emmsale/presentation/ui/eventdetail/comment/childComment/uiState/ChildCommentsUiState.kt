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

    fun changeToLoadingState(): ChildCommentsUiState = copy(
        isLoading = true,
        isFetchingError = false,
        isPostingError = false,
        isDeletionError = false,
    )

    fun changeToFetchingErrorState(): ChildCommentsUiState = copy(
        isLoading = false,
        isFetchingError = true,
        isPostingError = false,
        isDeletionError = false,
    )

    fun changeToPostingErrorState(): ChildCommentsUiState = copy(
        isLoading = false,
        isFetchingError = false,
        isPostingError = true,
        isDeletionError = false,
    )

    fun changeToDeleteErrorState(): ChildCommentsUiState = copy(
        isLoading = false,
        isFetchingError = false,
        isPostingError = false,
        isDeletionError = true,
    )

    fun changeChildCommentsState(
        comment: Comment,
        loginMemberId: Long,
    ): ChildCommentsUiState = copy(
        isLoading = false,
        isFetchingError = false,
        isPostingError = false,
        isDeletionError = false,
        parentComment = CommentUiState.create(comment, loginMemberId),
        childComments = comment.childComments.map { CommentUiState.create(it, loginMemberId) },
    )

    companion object {
        val FIRST_LOADING = ChildCommentsUiState(
            isLoading = true,
            isFetchingError = false,
            isPostingError = false,
            isDeletionError = false,
            parentComment = CommentUiState(
                authorId = -1,
                authorName = "",
                authorImageUrl = "",
                lastModifiedDate = "",
                isUpdated = false,
                id = -1,
                content = "",
                isUpdatable = false,
                isDeletable = false,
                isReportable = false,
                isDeleted = false,
            ),
            childComments = listOf(),
        )
    }
}
