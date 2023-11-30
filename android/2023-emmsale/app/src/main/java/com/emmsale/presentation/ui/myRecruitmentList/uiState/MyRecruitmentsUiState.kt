package com.emmsale.presentation.ui.myRecruitmentList.uiState

import com.emmsale.data.model.Recruitment

data class MyRecruitmentsUiState(
    val list: List<MyRecruitmentUiState> = listOf(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    companion object {
        fun from(recruitments: List<Recruitment>) = MyRecruitmentsUiState(
            list = recruitments.map { MyRecruitmentUiState.from(it) },
            isLoading = false,
            isError = false,
        )
    }
}
