package com.emmsale.presentation.ui.main.setting.notificationTagConfig.uistate

import com.emmsale.data.eventTag.EventTag

data class NotificationTagsConfigUiState(
    val conferenceTags: List<NotificationTagConfigUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isTagFetchingSuccess: Boolean = false,
    val isTagFetchingError: Boolean = false,
    val isInterestTagFetchingError: Boolean = false,
    val isInterestTagsUpdateSuccess: Boolean = false,
    val isInterestTagsUpdatingError: Boolean = false,
) {
    fun addInterestTagById(id: Long): NotificationTagsConfigUiState = copy(
        conferenceTags = conferenceTags.map { tag ->
            if (tag.id == id) tag.setChecked(true) else tag
        },
        isTagFetchingError = false,
        isInterestTagFetchingError = false,
        isInterestTagsUpdatingError = false,
    )

    fun removeInterestTagById(id: Long): NotificationTagsConfigUiState = copy(
        conferenceTags = conferenceTags.map { tag ->
            if (tag.id == id) tag.setChecked(false) else tag
        },
        isTagFetchingError = false,
        isInterestTagFetchingError = false,
        isInterestTagsUpdatingError = false,
    )

    companion object {
        fun from(
            eventTags: List<EventTag>,
            interestEventTags: List<EventTag>,
        ): NotificationTagsConfigUiState {
            val interestTagIds = interestEventTags.map(EventTag::id)

            return NotificationTagsConfigUiState(
                conferenceTags = eventTags.map { eventTag ->
                    NotificationTagConfigUiState.from(
                        eventTag = eventTag,
                        isSelected = eventTag.id in interestTagIds,
                    )
                },
                isTagFetchingSuccess = true,
            )
        }
    }
}
