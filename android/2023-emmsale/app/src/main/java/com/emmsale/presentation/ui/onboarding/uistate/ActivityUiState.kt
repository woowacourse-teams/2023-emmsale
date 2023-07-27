package com.emmsale.presentation.ui.onboarding.uistate

import com.emmsale.data.activity.Activity

data class ActivityUiState(
    val id: Int,
    val name: String,
    var isSelected: Boolean = false,
) {
    companion object {
        fun from(activity: Activity): ActivityUiState = ActivityUiState(
            id = activity.id,
            name = activity.name
        )
    }
}
