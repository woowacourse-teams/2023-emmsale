package com.emmsale.presentation.ui.myProfile.uiState

import com.emmsale.data.model.Activity
import com.emmsale.data.model.ActivityType.CLUB
import com.emmsale.data.model.ActivityType.EDUCATION
import com.emmsale.data.model.ActivityType.FIELD
import com.emmsale.data.model.Member

data class MyProfileUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val member: Member = Member(),
) {
    val fields: List<Activity>
        get() = member.activities.filter { it.activityType == FIELD }

    val educations: List<Activity>
        get() = member.activities.filter { it.activityType == EDUCATION }

    val clubs: List<Activity>
        get() = member.activities.filter { it.activityType == CLUB }

    fun changeMemberState(member: Member): MyProfileUiState = copy(
        isLoading = false,
        isError = false,
        member = member,
    )

    fun changeActivitiesState(activities: List<Activity>): MyProfileUiState = copy(
        isLoading = false,
        isError = false,
        member = member.copy(activities = activities),
    )

    fun changeToLoadingState(): MyProfileUiState = copy(
        isLoading = true,
    )

    fun changeToErrorState(): MyProfileUiState = copy(
        isError = true,
    )
}
