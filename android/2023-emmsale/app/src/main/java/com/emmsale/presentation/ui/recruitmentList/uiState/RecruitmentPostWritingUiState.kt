package com.emmsale.presentation.ui.recruitmentList.uiState

data class RecruitmentPostWritingUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isPostingSuccess: Boolean = false,
    val isEditingSuccess: Boolean = false,
    val writingMode: WritingModeUiState = WritingModeUiState.POST,
) {
    fun changeToLoadingState(): RecruitmentPostWritingUiState =
        copy(
            isLoading = true,
            isError = false,
            isPostingSuccess = false,
        )

    fun changeToErrorState(): RecruitmentPostWritingUiState =
        copy(
            isLoading = false,
            isError = true,
            isPostingSuccess = false,
        )

    fun changeToPostSuccessState(): RecruitmentPostWritingUiState =
        copy(
            isLoading = false,
            isError = false,
            isPostingSuccess = true,
        )

    fun changeToEditSuccessState(): RecruitmentPostWritingUiState =
        copy(
            isLoading = false,
            isError = false,
            isEditingSuccess = true,
        )

    fun setWritingMode(mode: WritingModeUiState): RecruitmentPostWritingUiState = copy(
        writingMode = mode,
    )
}
