package com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState

import com.emmsale.data.activity.Activity

data class SelectableActivityUiState(
    val id: Long,
    val name: String,
    val isSelected: Boolean,
) {

    companion object {
        fun from(activity: Activity, isSelected: Boolean = false) = SelectableActivityUiState(
            id = activity.id,
            name = activity.name,
            isSelected = isSelected,
        )
    }
}
