package com.emmsale.presentation.ui.main.event.conference.uistate

data class ConferenceSelectedFilteringUiState(
    val statusFilteringOptions: List<ConferenceSelectedFilteringOptionUiState> = emptyList(),
    val tagFilteringOptions: List<ConferenceSelectedFilteringOptionUiState> = emptyList(),
    val startDateFilteringOption: ConferenceSelectedFilteringDateOptionUiState? = null,
    val endDateFilteringOption: ConferenceSelectedFilteringDateOptionUiState? = null,
) {
    val selectedStatusFilteringOptionIds: Array<Long> = statusFilteringOptions
        .map { it.id }
        .toTypedArray()

    val selectedTagFilteringOptionIds: Array<Long> = tagFilteringOptions
        .map { it.id }
        .toTypedArray()

    fun removeFilteringOptionBy(filterId: Long): ConferenceSelectedFilteringUiState = copy(
        statusFilteringOptions = statusFilteringOptions.filterNot { filterOption ->
            filterOption.id == filterId
        },
        tagFilteringOptions = tagFilteringOptions.filterNot { filterOption ->
            filterOption.id == filterId
        },
    )

    fun clearSelectedDate(): ConferenceSelectedFilteringUiState = copy(
        startDateFilteringOption = null,
        endDateFilteringOption = null,
    )
}
