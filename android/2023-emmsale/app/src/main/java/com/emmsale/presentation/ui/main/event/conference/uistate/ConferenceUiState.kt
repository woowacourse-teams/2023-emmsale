package com.emmsale.presentation.ui.main.event.conference.uistate

import com.emmsale.data.conferenceStatus.ConferenceStatus
import com.emmsale.data.event.model.Conference

data class ConferenceUiState(
    val id: Long,
    val name: String,
    val tags: List<String>,
    val status: String,
    val posterUrl: String?,
    val isFree: Boolean,
    val isOnline: Boolean,
) {
    companion object {
        fun from(conference: Conference): ConferenceUiState = ConferenceUiState(
            id = conference.id,
            name = conference.name,
            tags = conference.tags,
            status = getStatus(conference),
            posterUrl = conference.posterUrl,
            isFree = conference.isFree,
            isOnline = conference.isOnline,
        )

        private fun getStatus(conference: Conference): String = when (conference.status) {
            ConferenceStatus.IN_PROGRESS -> "진행중"
            ConferenceStatus.SCHEDULED -> "D-${conference.dDay}"
            ConferenceStatus.ENDED -> "마감"
        }
    }
}
