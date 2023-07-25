package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.activity.Activities
import com.emmsale.presentation.ui.onboarding.ActivityCategory

sealed class ActivityTypeContentUiState {
    data class Success(val activities: List<ActivitiesUiState>) : ActivityTypeContentUiState() {
        fun findActivity(category: ActivityCategory): ActivitiesUiState? =
            activities.find { it.category == category.title }
    }

    object Error : ActivityTypeContentUiState()

    companion object {
        fun from(activitiesResult: List<Activities>): Success =
            Success(activities = ActivitiesUiState.from(activitiesResult))
    }
}
