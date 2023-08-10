package com.emmsale.presentation.ui.main.event.conference.uistate

import com.emmsale.data.conferenceStatus.ConferenceStatus
import com.emmsale.data.eventTag.EventTag

data class ConferenceSelectedFilteringOptionUiState(
    val id: Long,
    val name: String,
) {
    companion object {
        fun from(conferenceStatus: ConferenceStatus): ConferenceSelectedFilteringOptionUiState =
            ConferenceSelectedFilteringOptionUiState(
                id = conferenceStatus.id,
                name = conferenceStatus.toText(),
            )

        private fun ConferenceStatus.toText(): String = when (this) {
            ConferenceStatus.IN_PROGRESS -> "진행 중"
            ConferenceStatus.SCHEDULED -> "진행 예정"
            ConferenceStatus.ENDED -> "마감"
        }

        fun from(eventTag: EventTag): ConferenceSelectedFilteringOptionUiState =
            ConferenceSelectedFilteringOptionUiState(
                id = eventTag.id,
                name = eventTag.name,
            )
    }
}


