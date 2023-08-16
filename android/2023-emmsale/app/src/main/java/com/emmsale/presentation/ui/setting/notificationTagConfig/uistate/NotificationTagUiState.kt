package com.emmsale.presentation.ui.setting.notificationTagConfig.uistate

import com.emmsale.data.eventTag.EventTag

data class NotificationTagUiState(
    val id: Long,
    val tagName: String,
) {
    companion object {
        fun from(eventTag: EventTag): NotificationTagUiState = NotificationTagUiState(
            id = eventTag.id,
            tagName = eventTag.name,
        )
    }
}
