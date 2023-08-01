package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.activity.Activities

data class ActivitiesUiState(
    val category: String,
    val activities: List<ActivityUiState>,
) {
    companion object {
        fun from(activities: Activities): ActivitiesUiState = ActivitiesUiState(
            category = activities.category,
            activities = activities.activities.map(ActivityUiState::from),
        )
    }
}
