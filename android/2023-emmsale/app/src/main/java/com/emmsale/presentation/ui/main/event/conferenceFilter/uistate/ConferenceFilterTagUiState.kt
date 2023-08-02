package com.emmsale.presentation.ui.main.event.conferenceFilter.uistate

data class ConferenceFilterTagUiState(
    val id: Long,
    val name: String,
    var isSelected: Boolean = false,
)
