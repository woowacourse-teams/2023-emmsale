package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.activity.Activities

sealed class ActivityTypeContentUiState {
    data class Success(val activities: List<ActivitiesUiState>) : ActivityTypeContentUiState()
    object Error : ActivityTypeContentUiState()

    companion object {
        fun from(activitiesResult: List<Activities>): Success =
            Success(activities = activitiesResult.map(ActivitiesUiState::from))
    }
}
