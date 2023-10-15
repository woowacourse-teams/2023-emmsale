package com.emmsale.presentation.ui.editMyProfile.uiState

import com.emmsale.data.model.Member

data class EditMyProfileUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val member: Member = Member(),
) {

    val selectableFieldSize: Int
        get() {
            return if (member.fields.size >= MAX_FIELDS_COUNT) {
                0
            } else {
                MAX_FIELDS_COUNT - member.fields.size
            }
        }

    fun changeMemberState(member: Member): EditMyProfileUiState = copy(
        isLoading = false,
        isError = false,
        member = member,
    )

    fun changeToLoadingState(): EditMyProfileUiState = copy(
        isLoading = true,
    )

    fun changeToErrorState(): EditMyProfileUiState = copy(
        isError = true,
    )

    fun changeDescription(description: String): EditMyProfileUiState = copy(
        member = member.copy(description = description),
    )

    fun updateProfileImageUrl(profileImageUrl: String): EditMyProfileUiState = copy(
        member = member.copy(profileImageUrl = profileImageUrl),
    )

    companion object {
        val FIRST_LOADING = EditMyProfileUiState(
            isLoading = true,
            isError = false,
            member = Member(),
        )

        private const val MAX_FIELDS_COUNT = 4
    }
}
