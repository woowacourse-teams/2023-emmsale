package com.emmsale.presentation.ui.setting.uiState

import com.emmsale.data.model.Member

data class MemberUiState(
    val isLoading: Boolean,
    val isError: Boolean,
    val isDeleted: Boolean,
    val isLogout: Boolean,
    val id: Long,
    val imageUrl: String,
    val name: String,
    val githubId: String,
) {

    fun changeToLoadingState(): MemberUiState = copy(
        isLoading = true,
    )

    fun changeToErrorState(): MemberUiState = copy(
        isError = true,
    )

    fun changeToLogoutState(): MemberUiState = copy(
        isLoading = false,
        isError = false,
        isLogout = true,
    )

    fun changeMemberState(member: Member): MemberUiState = copy(
        isLoading = false,
        isError = false,
        id = member.id,
        imageUrl = member.imageUrl,
        name = member.name,
        githubId = member.githubUrl,
    )

    companion object {
        val FIRST_LOADING = MemberUiState(
            isLoading = true,
            isError = false,
            isDeleted = false,
            isLogout = false,
            id = -1,
            imageUrl = "",
            name = "",
            githubId = "",
        )
    }
}
