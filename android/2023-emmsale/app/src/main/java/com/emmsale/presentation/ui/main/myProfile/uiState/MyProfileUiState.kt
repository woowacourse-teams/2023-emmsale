package com.emmsale.presentation.ui.main.myProfile.uiState

data class MyProfileUiState(
    val isLoading: Boolean,
    val isFetchingError: Boolean,
    val memberId: Long,
    val memberName: String,
    val description: String,
    val memberImageUrl: String,
    val fields: List<ActivityUiState>,
    val educations: List<ActivityUiState>,
    val clubs: List<ActivityUiState>,
) {
    companion object {
        val Loading = MyProfileUiState(
            isLoading = true,
            isFetchingError = false,
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
