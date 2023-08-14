package com.emmsale.presentation.ui.eventdetail.recruitment.uistate

data class CompanionRequestUiState(
    val isLoading: Boolean = false,
    val isRequestError: Boolean = false,
    val isRequestSuccess: Boolean = false,
    val isAlreadyRequest: Boolean = false,
) {
    fun changeToLoadingState() = copy(
        isLoading = true,
        isRequestError = false,
        isRequestSuccess = false,
    )

    fun changeToErrorState() = copy(
        isLoading = false,
        isRequestError = true,
        isRequestSuccess = false,
    )

    fun changeToSuccessState() = copy(
        isLoading = false,
        isRequestError = false,
        isRequestSuccess = true,
    )

    fun setIsAlreadyRequestState(state: Boolean) = copy(
        isAlreadyRequest = state,
    )
}
