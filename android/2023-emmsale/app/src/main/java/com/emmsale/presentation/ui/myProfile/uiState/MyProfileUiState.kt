package com.emmsale.presentation.ui.myProfile.uiState

import com.emmsale.data.model.Member

data class MyProfileUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val member: Member = Member(),
) {

    fun changeMemberState(member: Member): MyProfileUiState = copy(
        isLoading = false,
        isError = false,
        member = member,
    )

    fun changeToLoadingState(): MyProfileUiState = copy(
        isLoading = true,
    )

    fun changeToErrorState(): MyProfileUiState = copy(
        isError = true,
    )
}
