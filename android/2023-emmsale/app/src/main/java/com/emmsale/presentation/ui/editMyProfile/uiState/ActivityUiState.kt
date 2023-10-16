package com.emmsale.presentation.ui.editMyProfile.uiState

import com.emmsale.data.model.Activity

data class ActivityUiState(
    val activity: Activity,
    val isSelected: Boolean,
) {
    fun toggleSelection(): ActivityUiState = copy(
        isSelected = !isSelected,
    )

    companion object {
        fun from(activity: Activity, isSelected: Boolean = false) = ActivityUiState(
            activity = activity,
            isSelected = isSelected,
        )
    }
}
