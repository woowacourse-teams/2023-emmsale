package com.emmsale.presentation.ui.main.setting.notification.uistate

import com.emmsale.data.eventTag.EventTag

data class NotificationTagsUiState(
    val conferenceTags: List<NotificationTagUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isTagFetchingSuccess: Boolean = false,
    val isTagFetchingError: Boolean = false,
    val isTagRemoveError: Boolean = false,
) {
    companion object {
        fun from(eventTags: List<EventTag>): NotificationTagsUiState = NotificationTagsUiState(
            conferenceTags = eventTags.map { eventTag ->
                NotificationTagUiState.from(eventTag = eventTag)
            },
            isTagFetchingSuccess = true,
        )
    }
}
