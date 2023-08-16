package com.emmsale.presentation.ui.setting.myPost.uiState

import com.emmsale.data.myPost.MyPost

data class MyPostUiState(
    val id: Long = DEFAULT_ID,
    val postId: Long = DEFAULT_ID,
    val eventId: Long = DEFAULT_ID,
    val eventName: String = "",
    val content: String = "",
    val updatedAt: String = "",
) {
    companion object {
        private const val DEFAULT_ID = -1L

        fun from(myPost: MyPost): MyPostUiState = MyPostUiState(
            id = myPost.id,
            postId = myPost.postId,
            eventId = myPost.eventId,
            eventName = myPost.eventName ?: "",
            content = myPost.content ?: "",
            updatedAt = myPost.updatedAt.toString(),
        )
    }
}
