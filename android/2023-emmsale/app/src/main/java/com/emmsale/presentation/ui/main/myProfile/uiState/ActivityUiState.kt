package com.emmsale.presentation.ui.main.myProfile.uiState

import com.emmsale.data.activity.Activity1

data class ActivityUiState(
    val id: Long,
    val name: String,
) {

    companion object {
        fun from(activity: Activity1): ActivityUiState =
            ActivityUiState(
                id = activity.id,
                name = activity.name,
            )
    }
}
