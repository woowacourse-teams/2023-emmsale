package com.emmsale.presentation.ui.conferenceList.uiState

import com.emmsale.model.ConferenceStatus
import com.emmsale.model.EventTag

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
            ConferenceStatus.UPCOMING -> "진행 예정"
            ConferenceStatus.ENDED -> "마감"
        }

        fun from(eventTag: EventTag): ConferenceSelectedFilteringOptionUiState =
            ConferenceSelectedFilteringOptionUiState(
                id = eventTag.id,
                name = eventTag.name,
            )
    }
}
