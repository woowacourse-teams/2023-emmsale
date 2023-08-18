package com.emmsale.presentation.ui.main.setting.myComments.uiState

import com.emmsale.data.comment.Comment

data class MyCommentsUiState(
    val isLoading: Boolean,
    val isError: Boolean,
    val comments: List<MyCommentUiState>,
) {

    fun changeToErrorState(): MyCommentsUiState = copy(
        isLoading = false,
        isError = true,
    )

    fun setCommentsState(newComments: List<Comment>, loginMemberId: Long): MyCommentsUiState = copy(
        isLoading = false,
        isError = false,
        comments = newComments.flatMap { comment ->
            comment.childComments.map(MyCommentUiState::from) + MyCommentUiState.from(comment)
        }.filter { it.authorId == loginMemberId && !it.isDeleted },
    )

    companion object {
        val FIRST_LOADING = MyCommentsUiState(
            isLoading = true,
            isError = false,
            comments = listOf(),
        )
    }
}
