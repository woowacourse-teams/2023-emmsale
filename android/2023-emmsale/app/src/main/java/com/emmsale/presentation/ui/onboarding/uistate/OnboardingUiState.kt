package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.activity.Activity
import com.emmsale.data.activity.ActivityType

data class OnboardingUiState(
    val fields: List<ActivityUiState> = emptyList(),
    val educations: List<ActivityUiState> = emptyList(),
    val clubs: List<ActivityUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingActivitiesFailed: Boolean = false,
    val memberSavingUiState: MemberSavingUiState = MemberSavingUiState.None,
) {
    val selectedActivityIds = (fields + educations + clubs)
        .filter { it.isSelected }
        .map { it.id }

    companion object {
        fun from(activities: List<Activity>): OnboardingUiState = OnboardingUiState(
            fields = activities.toUiState(ActivityType.FIELD),
            educations = activities.toUiState(ActivityType.EDUCATION),
            clubs = activities.toUiState(ActivityType.CLUB),
        )

        private fun List<Activity>.toUiState(activityType: ActivityType): List<ActivityUiState> =
            this
                .filter { it.activityType == activityType }
                .map(ActivityUiState::from)
    }
}
