package com.emmsale.presentation.ui.main.setting.myComments.uiState

import com.emmsale.data.comment.Comment

data class MyCommentsUiState(
    val isLoading: Boolean,
    val isFetchingError: Boolean,
    val comments: List<MyCommentUiState>,
) {

    fun changeToFetchingErrorState(): MyCommentsUiState = copy(
        isLoading = false,
        isFetchingError = true,
    )

    fun setCommentsState(newComments: List<Comment>, loginMemberId: Long): MyCommentsUiState = copy(
        isLoading = false,
        isFetchingError = false,
        comments = newComments.flatMap { comment ->
            comment.childComments.map(MyCommentUiState::from) + MyCommentUiState.from(comment)
        }.filter { it.authorId == loginMemberId && !it.isDeleted },
    )

    companion object {
        val FIRST_LOADING = MyCommentsUiState(
            isLoading = true,
            isFetchingError = false,
            comments = listOf(),
        )
    }
}
