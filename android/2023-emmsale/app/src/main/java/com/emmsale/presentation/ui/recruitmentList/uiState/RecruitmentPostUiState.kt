package com.emmsale.presentation.ui.recruitmentList.uiState

import com.emmsale.data.model.Recruitment

data class RecruitmentPostUiState(
    val id: Long = DEFAULT_RECRUITMENT_ID,
    val memberId: Long = DEFAULT_MEMBER_ID,
    val name: String = "",
    val profileImageUrl: String = "",
    val content: String = "",
    val updatedAt: String = "",
    val isMyPost: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    fun changeToLoadingState() = copy(
        isLoading = true,
    )

    fun changeToErrorState() = copy(
        isLoading = false,
        isError = true,
    )

    companion object {
        private const val DEFAULT_RECRUITMENT_ID = -1L
        private const val DEFAULT_MEMBER_ID = -1L
        fun from(recruitment: Recruitment): RecruitmentPostUiState = RecruitmentPostUiState(
            id = recruitment.id,
            memberId = recruitment.memberId,
            name = recruitment.name,
            profileImageUrl = recruitment.imageUrl,
            content = recruitment.content ?: "",
            updatedAt = recruitment.updatedDate.toString(),
            isMyPost = false,
            isLoading = false,
            isError = false,
        )
    }
}
