package com.emmsale.presentation.ui.myPostList.uiState

enum class PostType(val position: Int) {
    GENERAL(0), RECRUITMENT(1);

    companion object {
        fun from(position: Int): PostType? = PostType.values().find { it.position == position }
    }
}
