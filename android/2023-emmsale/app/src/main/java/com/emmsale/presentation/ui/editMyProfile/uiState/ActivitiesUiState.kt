package com.emmsale.presentation.ui.editMyProfile.uiState

import com.emmsale.data.model.Activity
import com.emmsale.data.model.ActivityType.CLUB
import com.emmsale.data.model.ActivityType.EDUCATION
import com.emmsale.data.model.ActivityType.FIELD

data class ActivitiesUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val activities: List<ActivityUiState> = emptyList(),
) {
    val fields = activities.filter { it.activity.activityType == FIELD }

    val selectedFieldsSize = fields.count { it.isSelected }

    val educations = activities.filter { it.activity.activityType == EDUCATION }

    val selectedEducationsSize = educations.count { it.isSelected }

    val clubs = activities.filter { it.activity.activityType == CLUB }

    val selectedClubsSize = clubs.count { it.isSelected }

    fun fetchUnSelectedActivities(
        allActivities: List<Activity>,
        myActivities: List<Activity>,
    ): ActivitiesUiState {
        val unSelectedActivities = allActivities
            .filterNot { myActivities.contains(it) }
            .map { ActivityUiState(it, false) }

        return copy(
            isLoading = false,
            isError = false,
            activities = unSelectedActivities,
        )
    }

    fun changeToErrorState(): ActivitiesUiState = copy(
        isLoading = false,
        isError = true,
    )

    fun changeToLoadingState(): ActivitiesUiState = copy(
        isLoading = true,
        isError = false,
    )

    fun toggleIsSelected(activityId: Long): ActivitiesUiState = copy(
        activities = activities.map { if (it.activity.id == activityId) it.toggleSelection() else it },
    )
}
