package com.emmsale.presentation.ui.main.event.conference.uistate

data class ConferenceSelectedFilteringUiState(
    val conferenceStatusFilteringOptions: List<ConferenceSelectedFilteringOptionUiState> = emptyList(),
    val conferenceTagFilteringOptions: List<ConferenceSelectedFilteringOptionUiState> = emptyList(),
    val selectedStartDate: ConferenceSelectedFilteringDateOptionUiState? = null,
    val selectedEndDate: ConferenceSelectedFilteringDateOptionUiState? = null,
) {
    val selectedStatusFilteringOptionIds: Array<Long> = conferenceStatusFilteringOptions
        .map { it.id }
        .toTypedArray()

    val selectedTagFilteringOptionIds: Array<Long> = conferenceTagFilteringOptions
        .map { it.id }
        .toTypedArray()
}
