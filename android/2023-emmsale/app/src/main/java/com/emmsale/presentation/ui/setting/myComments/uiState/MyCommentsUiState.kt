package com.emmsale.presentation.ui.setting.myComments.uiState

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

    fun setCommentsState(comments: List<Comment>): MyCommentsUiState = copy(
        isLoading = false,
        isFetchingError = false,
        comments = comments.map(MyCommentUiState::from),
    )

    companion object {
        val FIRST_LOADING = MyCommentsUiState(
            isLoading = true,
            isFetchingError = false,
            comments = listOf(),
        )
    }
}
