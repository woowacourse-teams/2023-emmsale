package com.emmsale.presentation.ui.profile.uiState

import com.emmsale.data.activity.Activity
import com.emmsale.data.activity.ActivityType
import com.emmsale.data.member.Member

data class ProfileUiState(
    val isLoading: Boolean,
    val isError: Boolean,
    val isLoginMember: Boolean,
    val memberId: Long,
    val memberName: String,
    val description: String,
    val memberImageUrl: String,
    val fields: List<ActivityUiState>,
    val educations: List<ActivityUiState>,
    val clubs: List<ActivityUiState>,
) {

    fun changeToLoadingState(): ProfileUiState = copy(
        isLoading = true,
        isError = false,
    )

    fun changeToFetchingErrorState(): ProfileUiState = copy(
        isLoading = false,
        isError = true,
    )

    fun changeMemberState(member: Member, loginMemberId: Long): ProfileUiState = copy(
        isLoading = false,
        isError = false,
        isLoginMember = member.id == loginMemberId,
        memberId = member.id,
        memberName = member.name,
        description = member.description,
        memberImageUrl = member.imageUrl,
    )

    fun changeActivityState(activities: List<Activity>): ProfileUiState = copy(
        isLoading = false,
        isError = false,
        fields = activities.getActivityUiStatesOf(ActivityType.FIELD),
        educations = activities.getActivityUiStatesOf(ActivityType.EDUCATION),
        clubs = activities.getActivityUiStatesOf(ActivityType.CLUB),
    )

    private fun List<Activity>.getActivityUiStatesOf(activityType: ActivityType): List<ActivityUiState> {
        return this.filter { it.activityType == activityType }
            .map { ActivityUiState.from(it) }
    }

    companion object {
        val FIRST_LOADING = ProfileUiState(
            isLoading = true,
            isError = false,
            isLoginMember = false,
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
