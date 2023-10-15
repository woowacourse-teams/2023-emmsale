package com.emmsale.presentation.ui.profile.uiState

import com.emmsale.data.model.Activity
import com.emmsale.data.model.Member

data class ProfileUiState(
    val isLoading: Boolean,
    val isError: Boolean,
    val isLoginMember: Boolean,
    val member: Member = Member(),
) {

    fun changeToLoadingState(): ProfileUiState = copy(
        isLoading = true,
        isError = false,
    )

    fun changeToFetchingErrorState(): ProfileUiState = copy(
        isLoading = false,
        isError = true,
    )

    fun changeMemberState(member: Member, loginMemberId: Long): ProfileUiState = copy(
        isLoading = false,
        isError = false,
        isLoginMember = member.id == loginMemberId,
        member = member,
    )

    fun changeActivityState(activities: List<Activity>): ProfileUiState = copy(
        isLoading = false,
        isError = false,
        member = member.copy(activities = activities),
    )

    companion object {
        val FIRST_LOADING = ProfileUiState(
            isLoading = true,
            isError = false,
            isLoginMember = false,
        )
    }
}
