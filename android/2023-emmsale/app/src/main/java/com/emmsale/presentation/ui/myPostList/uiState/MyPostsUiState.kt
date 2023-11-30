package com.emmsale.presentation.ui.myPostList.uiState

import com.emmsale.data.model.Recruitment

data class MyPostsUiState(
    val list: List<MyPostUiState> = listOf(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    companion object {
        fun from(recruitments: List<Recruitment>) = MyPostsUiState(
            list = recruitments.map { MyPostUiState.from(it) },
            isLoading = false,
            isError = false,
        )
    }
}
