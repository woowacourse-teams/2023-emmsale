package com.emmsale.presentation.ui.eventdetail.recruitment.uistate

import com.emmsale.data.recruitment.Recruitment

data class RecruitmentUiState(
    val id: Long = DEFAULT_RECRUITMENT_ID,
    val memberId: Long = DEFAULT_MEMBER_ID,
    val name: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val content: String = "",
    val updatedAt: String = "",
) {
    companion object {
        private const val DEFAULT_RECRUITMENT_ID = -1L
        private const val DEFAULT_MEMBER_ID = -1L
        fun from(recruitment: Recruitment): RecruitmentUiState = RecruitmentUiState(
            id = recruitment.id,
            memberId = recruitment.memberId,
            name = recruitment.name,
            imageUrl = recruitment.imageUrl,
            description = recruitment.description ?: "",
            content = recruitment.content ?: "",
            updatedAt = recruitment.updatedDate.toString(),
        )
    }
}
