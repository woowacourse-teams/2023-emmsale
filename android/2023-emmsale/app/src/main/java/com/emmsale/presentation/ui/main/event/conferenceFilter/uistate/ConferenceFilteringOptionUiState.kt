package com.emmsale.presentation.ui.main.event.conferenceFilter.uistate

import android.os.Parcelable
import com.emmsale.data.conferenceStatus.ConferenceStatus
import com.emmsale.data.eventTag.EventTag
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConferenceFilteringOptionUiState(
    val id: Long,
    val name: String,
    val isSelected: Boolean = false,
) : Parcelable {
    fun toggleSelection(): ConferenceFilteringOptionUiState = copy(isSelected = !isSelected)

    companion object {
        fun from(conferenceStatus: ConferenceStatus): ConferenceFilteringOptionUiState =
            ConferenceFilteringOptionUiState(
                id = conferenceStatus.id,
                name = conferenceStatus.toText(),
            )

        private fun ConferenceStatus.toText(): String = when(this) {
            ConferenceStatus.IN_PROGRESS -> "진행 중"
            ConferenceStatus.SCHEDULED -> "진행 예정"
            ConferenceStatus.ENDED -> "마감"
        }

        fun from(eventTag: EventTag): ConferenceFilteringOptionUiState =
            ConferenceFilteringOptionUiState(
                id = eventTag.id,
                name = eventTag.name,
            )
    }
}
