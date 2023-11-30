package com.emmsale.presentation.ui.myPostList.uiState

import com.emmsale.data.model.Recruitment

data class MyPostUiState(
    val postId: Long = DEFAULT_ID,
    val eventId: Long = DEFAULT_ID,
    val eventName: String = "",
    val content: String = "",
    val updatedAt: String = "",
) {
    companion object {
        private const val DEFAULT_ID = -1L

        fun from(recruitment: Recruitment) = MyPostUiState(
            postId = recruitment.id,
            eventId = recruitment.event.id,
            eventName = recruitment.event.name,
            content = recruitment.content,
            updatedAt = recruitment.updatedDate.toString(),
        )
    }
}
