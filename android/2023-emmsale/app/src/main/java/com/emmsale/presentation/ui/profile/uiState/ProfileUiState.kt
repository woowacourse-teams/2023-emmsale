package com.emmsale.presentation.ui.profile.uiState

data class ProfileUiState(
    val isLoading: Boolean,
    val isFetchingError: Boolean,
    val isLoginMember: Boolean,
    val memberId: Long,
    val memberName: String,
    val description: String,
    val memberImageUrl: String,
    val fields: List<ActivityUiState>,
    val educations: List<ActivityUiState>,
    val clubs: List<ActivityUiState>,
) {
    companion object {
        val Loading = ProfileUiState(
            isLoading = true,
            isFetchingError = false,
            isLoginMember = true,
            memberId = -1,
            memberName = "",
            description = "",
            memberImageUrl = "",
            fields = listOf(),
            educations = listOf(),
            clubs = listOf(),
        )
    }
}
