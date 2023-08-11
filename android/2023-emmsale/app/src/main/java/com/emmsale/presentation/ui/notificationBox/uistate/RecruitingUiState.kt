package com.emmsale.presentation.ui.notificationBox.uistate

data class RecruitingUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isAccepted: Boolean = false,
    val isRejected: Boolean = false,
) {
    fun changeToLoadingState(): RecruitingUiState = copy(
        isLoading = true,
        isError = false,
        isAccepted = false,
        isRejected = false,
    )

    fun changeToAcceptedState(): RecruitingUiState = copy(
        isLoading = false,
        isError = false,
        isAccepted = true,
        isRejected = false,
    )

    fun changeToRejectedState(): RecruitingUiState = copy(
        isLoading = false,
        isError = false,
        isAccepted = false,
        isRejected = true,
    )

    fun changeToErrorState(): RecruitingUiState = copy(
        isLoading = false,
        isError = true,
        isAccepted = false,
        isRejected = false,
    )
}
