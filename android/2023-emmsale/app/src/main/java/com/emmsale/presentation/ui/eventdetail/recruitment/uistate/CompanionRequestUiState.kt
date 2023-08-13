package com.emmsale.presentation.ui.eventdetail.recruitment.uistate

data class CompanionRequestUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val isAlreadyRequest: Boolean = false,
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

    fun setIsAlreadyRequestState(state: Boolean) = copy(
        isAlreadyRequest = state,
    )
}
