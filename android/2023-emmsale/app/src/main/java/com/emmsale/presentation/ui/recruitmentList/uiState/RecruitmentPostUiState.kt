package com.emmsale.presentation.ui.recruitmentList.uiState

import com.emmsale.data.model.RecruitmentPost

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

        fun from(recruitmentPost: RecruitmentPost): RecruitmentPostUiState = RecruitmentPostUiState(
            id = recruitmentPost.id,
            memberId = recruitmentPost.memberId,
            name = recruitmentPost.name,
            profileImageUrl = recruitmentPost.imageUrl,
            content = recruitmentPost.content ?: "",
            updatedAt = recruitmentPost.updatedDate.toString(),
            isMyPost = false,
            isLoading = false,
            isError = false,
        )

        fun create(recruitmentPost: RecruitmentPost, myUid: Long): RecruitmentPostUiState =
            RecruitmentPostUiState(
                id = recruitmentPost.id,
                memberId = recruitmentPost.memberId,
                name = recruitmentPost.name,
                profileImageUrl = recruitmentPost.imageUrl,
                content = recruitmentPost.content ?: "",
                updatedAt = recruitmentPost.updatedDate.toString(),
                isMyPost = recruitmentPost.memberId == myUid,
                isLoading = false,
                isError = false,
            )
    }
}
