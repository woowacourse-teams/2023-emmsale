package com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState

import com.emmsale.data.activity.Activity
import com.emmsale.data.activity.ActivityType
import com.emmsale.data.member.Member

data class EditMyProfileUiState(
    val isLoading: Boolean,
    val id: Long,
    val name: String,
    val imageUrl: String,
    val description: String,
    val fields: List<ActivityUiState>,
    val clubs: List<ActivityUiState>,
    val educations: List<ActivityUiState>,
) {
    fun changeMemberState(member: Member): EditMyProfileUiState = copy(
        isLoading = false,
        id = member.id,
        name = member.name,
        imageUrl = member.imageUrl,
        description = member.description,
    )

    fun changeActivities(activities: List<Activity>): EditMyProfileUiState = copy(
        isLoading = false,
        fields = activities.getActivityUiStatesOf(ActivityType.FIELD),
        clubs = activities.getActivityUiStatesOf(ActivityType.CLUB),
        educations = activities.getActivityUiStatesOf(ActivityType.EDUCATION),
    )

    private fun List<Activity>.getActivityUiStatesOf(activityType: ActivityType): List<ActivityUiState> {
        return this.filter { it.activityType == activityType }
            .map(ActivityUiState::from)
    }

    fun changeDescription(description: String): EditMyProfileUiState = copy(
        isLoading = false,
        description = description,
    )

    companion object {
        val FIRST_LOADING = EditMyProfileUiState(
            isLoading = true,
            id = -1,
            name = "",
            imageUrl = "",
            description = "",
            fields = listOf(),
            clubs = listOf(),
            educations = listOf(),
        )
    }
}
