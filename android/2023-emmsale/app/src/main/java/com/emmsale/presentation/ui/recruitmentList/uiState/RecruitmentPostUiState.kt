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

        val Loading: RecruitmentPostUiState = RecruitmentPostUiState(
            id = DEFAULT_RECRUITMENT_ID,
            memberId = DEFAULT_MEMBER_ID,
            name = "",
            profileImageUrl = "",
            content = "",
            updatedAt = "",
            isMyPost = false,
            isLoading = true,
            isError = false,
        )

        fun from(recruitment: Recruitment): RecruitmentPostUiState = RecruitmentPostUiState(
            id = recruitment.id,
            memberId = recruitment.writer.id,
            name = recruitment.writer.name,
            profileImageUrl = recruitment.writer.profileImageUrl,
            content = recruitment.content ?: "",
            updatedAt = recruitment.updatedDate.toString(),
            isMyPost = false,
            isLoading = false,
            isError = false,
        )

        fun create(recruitment: Recruitment, myUid: Long): RecruitmentPostUiState =
            RecruitmentPostUiState(
                id = recruitment.id,
                memberId = recruitment.writer.id,
                name = recruitment.writer.name,
                profileImageUrl = recruitment.writer.profileImageUrl,
                content = recruitment.content ?: "",
                updatedAt = recruitment.updatedDate.toString(),
                isMyPost = recruitment.writer.id == myUid,
                isLoading = false,
                isError = false,
            )
    }
}
