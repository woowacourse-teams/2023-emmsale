package com.emmsale.presentation.ui.main.setting.notificationTagConfig.uistate

import com.emmsale.data.eventTag.EventTag

data class NotificationTagsConfigUiState(
    val conferenceTags: List<NotificationTagConfigUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    fun addInterestTagById(id: Long): NotificationTagsConfigUiState = copy(
        conferenceTags = conferenceTags.map { tag ->
            if (tag.id == id) tag.setChecked(true) else tag
        },
        isError = false,
    )

    fun removeInterestTagById(id: Long): NotificationTagsConfigUiState = copy(
        conferenceTags = conferenceTags.map { tag ->
            if (tag.id == id) tag.setChecked(false) else tag
        },
        isError = false,
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
                isError = false,
            )
        }
    }
}
