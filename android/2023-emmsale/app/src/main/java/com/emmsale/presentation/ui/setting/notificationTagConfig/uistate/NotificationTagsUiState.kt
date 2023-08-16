package com.emmsale.presentation.ui.setting.notificationTagConfig.uistate

import com.emmsale.data.eventTag.EventTag

data class NotificationTagsUiState(
    val notificationTags: List<NotificationTagUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isTagFetchingError: Boolean = false,
    val isInterestTagFetchingError: Boolean = false,
) {
    companion object {
        val TAG_FETCHING_ERROR = NotificationTagsUiState(isTagFetchingError = true)
        val INTEREST_TAG_FETCHING_ERROR = NotificationTagsUiState(isInterestTagFetchingError = true)

        fun from(eventTags: List<EventTag>, interestTagIds: List<Long>): NotificationTagsUiState =
            NotificationTagsUiState(
                notificationTags = eventTags.map { eventTag ->
                    NotificationTagUiState.from(
                        eventTag = eventTag,
                        isSelected = eventTag.id in interestTagIds,
                    )
                },
            )
    }
}
