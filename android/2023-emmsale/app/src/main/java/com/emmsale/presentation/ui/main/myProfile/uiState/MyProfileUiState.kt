package com.emmsale.presentation.ui.main.myProfile.uiState

import com.emmsale.data.activity.Activity
import com.emmsale.data.activity.ActivityType
import com.emmsale.data.member.Member
import com.emmsale.presentation.ui.profile.uiState.ActivityUiState

data class MyProfileUiState(
    val memberId: Long = -1,
    val memberName: String = "",
    val description: String = "",
    val memberImageUrl: String = "",
    val fields: List<ActivityUiState> = listOf(),
    val educations: List<ActivityUiState> = listOf(),
    val clubs: List<ActivityUiState> = listOf(),
) {
    fun changeMemberState(member: Member): MyProfileUiState = copy(
        memberId = member.id,
        memberName = member.name,
        description = member.description,
        memberImageUrl = member.imageUrl,
    )

    fun changeActivitiesState(activities: List<Activity>): MyProfileUiState = copy(
        fields = activities.getActivityUiStatesOf(ActivityType.FIELD),
        educations = activities.getActivityUiStatesOf(ActivityType.EDUCATION),
        clubs = activities.getActivityUiStatesOf(ActivityType.CLUB),
    )

    private fun List<Activity>.getActivityUiStatesOf(activityType: ActivityType): List<ActivityUiState> {
        return this.filter { it.activityType == activityType }
            .map { ActivityUiState.from(it) }
    }
}
