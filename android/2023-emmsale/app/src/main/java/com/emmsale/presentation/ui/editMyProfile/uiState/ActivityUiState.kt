package com.emmsale.presentation.ui.editMyProfile.uiState

import com.emmsale.data.model.Activity

data class ActivityUiState(
    val id: Long,
    val name: String,
) {
    companion object {
        fun from(activity: Activity) = ActivityUiState(
            id = activity.id,
            name = activity.name,
        )
    }
}
