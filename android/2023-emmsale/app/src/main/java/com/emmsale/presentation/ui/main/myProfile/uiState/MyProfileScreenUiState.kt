package com.emmsale.presentation.ui.main.myProfile.uiState

data class MyProfileScreenUiState(
    val isLoading: Boolean,
    val isProfileFetchingError: Boolean,
    val memberId: Long,
    val memberName: String,
    val description: String,
    val memberImageUrl: String,
    val fields: List<ActivityUiState>,
    val educations: List<ActivityUiState>,
    val clubs: List<ActivityUiState>,
    val isNotLogin: Boolean = false,
) {
    companion object {
        val Loading = MyProfileScreenUiState(
            isLoading = true,
            isProfileFetchingError = false,
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
