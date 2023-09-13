package com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState

import com.emmsale.data.model.Comment

data class ChildCommentsUiState(
    val isLoading: Boolean,
    val isError: Boolean,
    val parentComment: CommentUiState,
    val childComments: List<CommentUiState>,
) {

    fun changeToLoadingState(): ChildCommentsUiState = copy(
        isLoading = true,
        isError = false,
    )

    fun changeToErrorState(): ChildCommentsUiState = copy(
        isLoading = false,
        isError = true,
    )

    fun changeChildCommentsState(
        comment: Comment,
        loginMemberId: Long,
    ): ChildCommentsUiState = copy(
        isLoading = false,
        isError = false,
        parentComment = CommentUiState.create(comment, loginMemberId),
        childComments = comment.childComments.map { CommentUiState.create(it, loginMemberId) },
    )

    companion object {
        val FIRST_LOADING = ChildCommentsUiState(
            isLoading = true,
            isError = false,
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
