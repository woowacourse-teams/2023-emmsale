package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate

data class RecruitmentUiState(
    val isLoading: Boolean = false,
    val isChangingRecruitmentStatusFailed: Boolean = false,
    val isAccepted: Boolean = false,
    val isRejected: Boolean = false,
) {
    fun changeToLoadingState(): RecruitmentUiState = copy(
        isLoading = true,
        isChangingRecruitmentStatusFailed = false,
        isAccepted = false,
        isRejected = false,
    )

    fun changeToAcceptedState(): RecruitmentUiState = copy(
        isLoading = false,
        isChangingRecruitmentStatusFailed = false,
        isAccepted = true,
        isRejected = false,
    )

    fun changeToRejectedState(): RecruitmentUiState = copy(
        isLoading = false,
        isChangingRecruitmentStatusFailed = false,
        isAccepted = false,
        isRejected = true,
    )

    fun changeToUpdatingRecruitmentStatusErrorState(): RecruitmentUiState = copy(
        isLoading = false,
        isChangingRecruitmentStatusFailed = true,
        isAccepted = false,
        isRejected = false,
    )
}
