package com.emmsale.presentation.ui.recruitmentList.uiState

import com.emmsale.data.model.Recruitment
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class RecruitmentPostsUiState(
    val list: List<RecruitmentPostUiState> = listOf(),
    override val fetchResult: FetchResult = FetchResult.LOADING,
) : FetchResultUiState() {
    fun changeToLoadingState() = copy(
        fetchResult = FetchResult.LOADING,
    )

    fun changeToErrorState() = copy(
        fetchResult = FetchResult.ERROR,
    )

    companion object {
        fun from(recruitments: List<Recruitment>): RecruitmentPostsUiState {
            return RecruitmentPostsUiState(
                list = recruitments.map(RecruitmentPostUiState::from),
                fetchResult = FetchResult.SUCCESS,
            )
        }
    }
}
