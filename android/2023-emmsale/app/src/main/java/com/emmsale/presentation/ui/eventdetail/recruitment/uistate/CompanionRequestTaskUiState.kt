package com.emmsale.presentation.ui.eventdetail.recruitment.uistate

data class CompanionRequestTaskUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
) {
    fun changeToLoadingState() = copy(
        isLoading = true,
        isError = false,
        isSuccess = false,
    )

    fun changeToErrorState() = copy(
        isLoading = false,
        isError = true,
        isSuccess = false,
    )

    fun changeToSuccessState() = copy(
        isLoading = false,
        isError = false,
        isSuccess = true,
    )
}
