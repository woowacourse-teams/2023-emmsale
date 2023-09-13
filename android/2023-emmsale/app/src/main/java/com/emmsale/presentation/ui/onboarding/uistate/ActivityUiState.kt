package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.model.Activity

data class ActivityUiState(
    val id: Long,
    val name: String,
    val isSelected: Boolean = false,
) {
    companion object {
        fun from(activity: Activity): ActivityUiState = ActivityUiState(
            id = activity.id,
            name = activity.name,
        )
    }
}
