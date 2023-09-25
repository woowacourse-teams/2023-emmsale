package com.emmsale.presentation.ui.conferenceList.uiState

import com.emmsale.data.model.Conference

data class ConferenceUiState(
    val conference: Conference,
) {
    companion object {
        fun from(conference: Conference): ConferenceUiState = ConferenceUiState(
            conference,
        )
    }
}
