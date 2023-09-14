package com.emmsale.presentation.ui.myPostList.uiState

import com.emmsale.data.model.MyPost

data class MyPostsUiState(
    val list: List<MyPostUiState> = listOf(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    companion object {
        fun from(myPosts: List<MyPost>): MyPostsUiState {
            return MyPostsUiState(
                myPosts.map { MyPostUiState.from(it) },
                isLoading = false,
                isError = false,
            )
        }
    }
}
