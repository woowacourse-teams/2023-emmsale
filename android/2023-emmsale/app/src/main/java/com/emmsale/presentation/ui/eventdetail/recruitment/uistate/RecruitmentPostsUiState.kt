package com.emmsale.presentation.ui.eventdetail.recruitment.uistate

import com.emmsale.data.recruitment.Recruitment

data class RecruitmentPostsUiState(
    val list: List<RecruitmentPostUiState> = listOf(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    fun changeToLoadingState() = copy(
        isLoading = true,
        isError = false,
    )

    fun changeToErrorState() = copy(
        isLoading = false,
        isError = true,
    )

    companion object {
        fun from(recruitments: List<Recruitment>): RecruitmentPostsUiState {
            return RecruitmentPostsUiState(
                list = recruitments.map { RecruitmentPostUiState.from(it) },
                isLoading = false,
                isError = false,
            )
        }
    }
}
