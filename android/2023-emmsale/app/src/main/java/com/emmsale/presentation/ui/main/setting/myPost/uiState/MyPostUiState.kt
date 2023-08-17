package com.emmsale.presentation.ui.main.setting.myPost.uiState

import com.emmsale.data.myPost.MyPost

data class MyPostUiState(
    val postId: Long = DEFAULT_ID,
    val eventId: Long = DEFAULT_ID,
    val eventName: String = "",
    val content: String = "",
    val updatedAt: String = "",
) {
    companion object {
        private const val DEFAULT_ID = -1L

        fun from(myPost: MyPost): MyPostUiState = MyPostUiState(
            postId = myPost.postId,
            eventId = myPost.eventId,
            eventName = myPost.eventName ?: "",
            content = myPost.content ?: "",
            updatedAt = myPost.updatedAt.toString(),
        )
    }
}
