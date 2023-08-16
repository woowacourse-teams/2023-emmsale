package com.emmsale.presentation.ui.setting.notificationTagConfig.uistate

import com.emmsale.data.eventTag.EventTag

data class ConferenceNotificationTagUiState(
    val id: Long,
    val tagName: String,
    val isChecked: Boolean,
) {
    fun setChecked(isChecked: Boolean): ConferenceNotificationTagUiState =
        copy(isChecked = isChecked)

    companion object {
        fun from(eventTag: EventTag, isSelected: Boolean): ConferenceNotificationTagUiState =
            ConferenceNotificationTagUiState(
                id = eventTag.id,
                tagName = eventTag.name,
                isChecked = isSelected,
            )
    }
}
