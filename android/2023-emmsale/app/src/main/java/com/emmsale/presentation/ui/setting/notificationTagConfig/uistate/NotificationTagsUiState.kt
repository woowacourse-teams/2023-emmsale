package com.emmsale.presentation.ui.setting.notificationTagConfig.uistate

import com.emmsale.data.eventTag.EventTag

data class NotificationTagsUiState(
    val conferenceTags: List<ConferenceNotificationTagUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isTagFetchingSuccess: Boolean = false,
    val isTagFetchingError: Boolean = false,
    val isInterestTagFetchingError: Boolean = false,
    val isInterestTagsUpdateSuccess: Boolean = false,
    val isInterestTagsUpdatingError: Boolean = false,
) {
    val toData: List<Long> =
        conferenceTags.filter(ConferenceNotificationTagUiState::isChecked)
            .map(ConferenceNotificationTagUiState::id)

    fun addInterestTagById(id: Long): NotificationTagsUiState = copy(
        conferenceTags = conferenceTags.map { tag ->
            if (tag.id == id) tag.setChecked(true) else tag
        },
        isTagFetchingError = false,
        isInterestTagFetchingError = false,
        isInterestTagsUpdatingError = false,
    )

    fun removeInterestTagById(id: Long): NotificationTagsUiState = copy(
        conferenceTags = conferenceTags.map { tag ->
            if (tag.id == id) tag.setChecked(false) else tag
        },
        isTagFetchingError = false,
        isInterestTagFetchingError = false,
        isInterestTagsUpdatingError = false,
    )

    companion object {
        fun from(eventTags: List<EventTag>, interestTagIds: List<Long>): NotificationTagsUiState =
            NotificationTagsUiState(
                conferenceTags = eventTags.map { eventTag ->
                    ConferenceNotificationTagUiState.from(
                        eventTag = eventTag,
                        isSelected = eventTag.id in interestTagIds,
                    )
                },
                isTagFetchingSuccess = true,
            )
    }
}
