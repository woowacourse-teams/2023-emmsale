package com.emmsale.presentation.ui.myProfile.uiState

import com.emmsale.data.model.Activity
import com.emmsale.data.model.ActivityType
import com.emmsale.data.model.Member
import com.emmsale.presentation.ui.profile.uiState.ActivityUiState

data class MyProfileUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val memberId: Long = -1,
    val memberName: String = "",
    val description: String = "",
    val memberImageUrl: String = "",
    val fields: List<ActivityUiState> = listOf(),
    val educations: List<ActivityUiState> = listOf(),
    val clubs: List<ActivityUiState> = listOf(),
) {
    fun changeMemberState(member: Member): MyProfileUiState = copy(
        isLoading = false,
        isError = false,
        memberId = member.id,
        memberName = member.name,
        description = member.description,
        memberImageUrl = member.profileImageUrl,
    )

    fun changeActivitiesState(activities: List<Activity>): MyProfileUiState = copy(
        isLoading = false,
        isError = false,
        fields = activities.getActivityUiStatesOf(ActivityType.FIELD),
        educations = activities.getActivityUiStatesOf(ActivityType.EDUCATION),
        clubs = activities.getActivityUiStatesOf(ActivityType.CLUB),
    )

    fun changeToLoadingState(): MyProfileUiState = copy(
        isLoading = true,
    )

    fun changeToErrorState(): MyProfileUiState = copy(
        isError = true,
    )

    private fun List<Activity>.getActivityUiStatesOf(activityType: ActivityType): List<ActivityUiState> {
        return this.filter { it.activityType == activityType }
            .map { ActivityUiState.from(it) }
    }
}
