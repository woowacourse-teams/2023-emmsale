package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate

data class RecruitmentUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isAccepted: Boolean = false,
    val isRejected: Boolean = false,
) {
    fun changeToLoadingState(): RecruitmentUiState = copy(
        isLoading = true,
        isError = false,
        isAccepted = false,
        isRejected = false,
    )

    fun changeToAcceptedState(): RecruitmentUiState = copy(
        isLoading = false,
        isError = false,
        isAccepted = true,
        isRejected = false,
    )

    fun changeToRejectedState(): RecruitmentUiState = copy(
        isLoading = false,
        isError = false,
        isAccepted = false,
        isRejected = true,
    )

    fun changeToErrorState(): RecruitmentUiState = copy(
        isLoading = false,
        isError = true,
        isAccepted = false,
        isRejected = false,
    )
}
