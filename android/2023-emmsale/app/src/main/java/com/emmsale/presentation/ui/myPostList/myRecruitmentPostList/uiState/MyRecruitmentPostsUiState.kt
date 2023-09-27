package com.emmsale.presentation.ui.myPostList.myRecruitmentPostList.uiState

import com.emmsale.data.model.MyRecruitmentPost
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class MyRecruitmentPostsUiState(
    override val fetchResult: FetchResult = FetchResult.LOADING,
    val successData: List<MyRecruitmentPost> = emptyList(),
) : FetchResultUiState()
