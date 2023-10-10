package com.emmsale.presentation.ui.editMyProfile.uiState

import com.emmsale.data.model.Activity
import com.emmsale.data.model.ActivityType
import com.emmsale.data.model.Member

data class EditMyProfileUiState(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val description: String,
    val fields: List<ActivityUiState>,
    val clubs: List<ActivityUiState>,
    val educations: List<ActivityUiState>,
) {
    fun changeMemberState(member: Member): EditMyProfileUiState = copy(
        id = member.id,
        name = member.name,
        imageUrl = member.profileImageUrl,
        description = member.description,
    )

    fun changeActivities(activities: List<Activity>): EditMyProfileUiState = copy(
        fields = activities.getActivityUiStatesOf(ActivityType.FIELD),
        clubs = activities.getActivityUiStatesOf(ActivityType.CLUB),
        educations = activities.getActivityUiStatesOf(ActivityType.EDUCATION),
    )

    private fun List<Activity>.getActivityUiStatesOf(activityType: ActivityType): List<ActivityUiState> {
        return this.filter { it.activityType == activityType }
            .map(ActivityUiState.Companion::from)
    }

    fun changeDescription(description: String): EditMyProfileUiState = copy(
        description = description,
    )

    fun updateProfileImageUrl(profileImageUrl: String): EditMyProfileUiState = copy(
        imageUrl = profileImageUrl,
    )

    companion object {
        val FIRST_LOADING = EditMyProfileUiState(
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
