package com.emmsale.presentation.ui.main.setting.notificationTagConfig.uistate

import com.emmsale.data.model.EventTag

data class NotificationTagConfigUiState(
    val id: Long,
    val tagName: String,
    val isChecked: Boolean,
) {
    fun setChecked(isChecked: Boolean): NotificationTagConfigUiState =
        copy(isChecked = isChecked)

    companion object {
        fun from(eventTag: EventTag, isSelected: Boolean): NotificationTagConfigUiState =
            NotificationTagConfigUiState(
                id = eventTag.id,
                tagName = eventTag.name,
                isChecked = isSelected,
            )
    }
}
