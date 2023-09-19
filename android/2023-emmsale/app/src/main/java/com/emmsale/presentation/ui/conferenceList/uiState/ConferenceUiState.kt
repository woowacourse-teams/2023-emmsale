package com.emmsale.presentation.ui.conferenceList.uiState

import com.emmsale.data.model.Conference

data class ConferenceUiState(
    val conference: Conference,
) {
    fun getStatusString(): String = when (conference.status) {
        Conference.Status.IN_PROGRESS -> "진행중"
        Conference.Status.UPCOMING -> "D-${conference.dDay}"
        Conference.Status.ENDED -> "마감"
    }

    companion object {
        fun from(conference: Conference): ConferenceUiState = ConferenceUiState(
            conference,
        )
    }
}
