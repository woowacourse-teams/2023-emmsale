package com.emmsale.presentation.ui.editMyProfile.uiState

import com.emmsale.data.model.Member

data class EditMyProfileUiState(
    val member: Member = Member(),
) {

    fun changeDescription(description: String): EditMyProfileUiState = copy(
        member = member.copy(description = description),
    )

    fun updateProfileImageUrl(profileImageUrl: String): EditMyProfileUiState = copy(
        member = member.copy(profileImageUrl = profileImageUrl),
    )
}
