package com.emmsale.presentation.ui.notificationTagConfig.uiState

import com.emmsale.model.EventTag

data class NotificationTagsConfigUiState(
    val eventTags: List<NotificationTagConfigUiState> = emptyList(),
) {

    constructor(eventTags: List<EventTag>, interestEventTags: List<EventTag>) : this(
        eventTags.map { NotificationTagConfigUiState(it, it in interestEventTags) },
    )

    fun checkTag(tagId: Long) = NotificationTagsConfigUiState(
        eventTags = eventTags.map {
            if (it.eventTag.id == tagId) it.copy(isChecked = true) else it
        },
    )

    fun uncheckTag(tagId: Long) = NotificationTagsConfigUiState(
        eventTags = eventTags.map {
            if (it.eventTag.id == tagId) it.copy(isChecked = false) else it
        },
    )
}
