package com.emmsale.presentation.ui.conferenceList.uiState

import com.emmsale.data.model.Conference
import com.emmsale.data.model.Status

data class ConferenceUiState(
    val conference: Conference,
) {
    fun getStatusString(): String = when (conference.status) {
        Status.IN_PROGRESS -> "진행중"
        Status.UPCOMING -> "D-${conference.dDay}"
        Status.ENDED -> "마감"
    }

    companion object {
        fun from(conference: Conference): ConferenceUiState = ConferenceUiState(
            conference,
        )
    }
}
