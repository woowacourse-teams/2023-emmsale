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

    fun removeFilteringOptionBy(filterId: Long): ConferenceSelectedFilteringUiState = copy(
        conferenceStatusFilteringOptions = conferenceStatusFilteringOptions.filterNot { filterOption ->
            filterOption.id == filterId
        },
        conferenceTagFilteringOptions = conferenceTagFilteringOptions.filterNot { filterOption ->
            filterOption.id == filterId
        },
    )

    fun clearSelectedDate(): ConferenceSelectedFilteringUiState = copy(
        selectedStartDate = null,
        selectedEndDate = null,
    )
}
