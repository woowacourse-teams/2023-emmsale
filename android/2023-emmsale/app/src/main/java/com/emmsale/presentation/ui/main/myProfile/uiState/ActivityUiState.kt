package com.emmsale.presentation.ui.main.myProfile.uiState

import com.emmsale.data.activity.Activity

data class ActivityUiState(
    val id: Long,
    val name: String,
) {

    companion object {
        fun from(activity: Activity): ActivityUiState =
            ActivityUiState(
                id = activity.id,
                name = activity.name,
            )
    }
}
