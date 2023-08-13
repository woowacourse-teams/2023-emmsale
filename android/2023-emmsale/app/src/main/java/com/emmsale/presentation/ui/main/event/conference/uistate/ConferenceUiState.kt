package com.emmsale.presentation.ui.main.event.conference.uistate

import com.emmsale.data.conference.Conference
import com.emmsale.data.conferenceStatus.ConferenceStatus

data class ConferenceUiState(
    val id: Long,
    val name: String,
    val tags: List<String>,
    val status: String,
    val posterUrl: String?,
) {
    companion object {
        fun from(conference: Conference): ConferenceUiState = ConferenceUiState(
            id = conference.id,
            name = conference.name,
            tags = conference.tags,
            status = getStatus(conference),
            posterUrl = conference.posterUrl,
        )

        private fun getStatus(conference: Conference): String = when (conference.status) {
            ConferenceStatus.IN_PROGRESS -> "진행중"
            ConferenceStatus.SCHEDULED -> "D-${conference.dDay}"
            ConferenceStatus.ENDED -> "마감"
        }
    }
}
