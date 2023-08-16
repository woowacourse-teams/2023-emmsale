package com.emmsale.presentation.ui.setting.notificationTagConfig.uistate

import com.emmsale.data.eventTag.EventTag

data class NotificationTagsUiState(
    val notificationTags: List<NotificationTagUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isFetchingError: Boolean = false,
) {
    companion object {
        val FETCHING_ERROR = NotificationTagsUiState(isFetchingError = true)

        fun from(eventTags: List<EventTag>): NotificationTagsUiState = NotificationTagsUiState(
            notificationTags = eventTags.map(NotificationTagUiState::from),
        )
    }
}
