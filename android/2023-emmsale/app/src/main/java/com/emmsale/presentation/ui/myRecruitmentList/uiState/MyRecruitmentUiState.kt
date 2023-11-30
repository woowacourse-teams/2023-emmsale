package com.emmsale.presentation.ui.myRecruitmentList.uiState

import com.emmsale.data.model.Recruitment

data class MyRecruitmentUiState(
    val postId: Long = DEFAULT_ID,
    val eventId: Long = DEFAULT_ID,
    val eventName: String = "",
    val content: String = "",
    val updatedAt: String = "",
) {
    companion object {
        private const val DEFAULT_ID = -1L

        fun from(recruitment: Recruitment) = MyRecruitmentUiState(
            postId = recruitment.id,
            eventId = recruitment.event.id,
            eventName = recruitment.event.name,
            content = recruitment.content,
            updatedAt = recruitment.updatedDate.toString(),
        )
    }
}
