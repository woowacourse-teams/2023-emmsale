package com.emmsale.presentation.ui.main.myProfile.uiState

import com.emmsale.data.activity.Activity
import com.emmsale.data.activity.ActivityType
import com.emmsale.data.member.Member
import com.emmsale.presentation.ui.profile.uiState.ActivityUiState

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

    fun changeToLoadingState(): MyProfileUiState = copy(
        isLoading = true,
        isFetchingError = false,
    )

    fun changeToFetchingErrorState(): MyProfileUiState = copy(
        isLoading = false,
        isFetchingError = true,
    )

    fun changeMemberState(member: Member): MyProfileUiState = copy(
        isLoading = false,
        isFetchingError = false,
        memberId = member.id,
        memberName = member.name,
        description = member.description,
        memberImageUrl = member.imageUrl,
    )

    fun changeActivitiesState(activities: List<Activity>): MyProfileUiState = copy(
        isLoading = false,
        isFetchingError = false,
        fields = activities.getActivityUiStatesOf(ActivityType.FIELD),
        educations = activities.getActivityUiStatesOf(ActivityType.EDUCATION),
        clubs = activities.getActivityUiStatesOf(ActivityType.CLUB),
    )

    private fun List<Activity>.getActivityUiStatesOf(activityType: ActivityType): List<ActivityUiState> {
        return this.filter { it.activityType == activityType }
            .map { ActivityUiState.from(it) }
    }

    companion object {
        val FIRST_LOADING = MyProfileUiState(
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
