package com.emmsale.presentation.ui.editMyProfile.uiState

import com.emmsale.data.model.Member

data class EditMyProfileUiState(
    val member: Member = Member(),
) {

    val selectableFieldSize: Int
        get() = (MAX_FIELDS_COUNT - member.fields.size).coerceAtLeast(0)

    fun changeDescription(description: String): EditMyProfileUiState = copy(
        member = member.copy(description = description),
    )

    fun updateProfileImageUrl(profileImageUrl: String): EditMyProfileUiState = copy(
        member = member.copy(profileImageUrl = profileImageUrl),
    )

    companion object {
        private const val MAX_FIELDS_COUNT = 4
    }
}
