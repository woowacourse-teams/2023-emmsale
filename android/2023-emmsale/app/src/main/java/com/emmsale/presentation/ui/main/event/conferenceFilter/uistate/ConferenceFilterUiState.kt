package com.emmsale.presentation.ui.main.event.conferenceFilter.uistate

data class ConferenceFilterUiState(
    val conferenceStatusFilteringOptions: List<ConferenceFilteringOptionUiState> = emptyList(),
    val conferenceTagFilteringOptions: List<ConferenceFilteringOptionUiState> = emptyList(),
    val selectedStartDate: ConferenceFilteringDateOptionUiState? = null,
    val selectedEndDate: ConferenceFilteringDateOptionUiState? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    val selectedStatusFilteringOptionIds: Array<Long> = conferenceStatusFilteringOptions
        .filter { it.isSelected }
        .map { it.id }
        .toTypedArray()

    val selectedTagFilteringOptionIds: Array<Long> = conferenceTagFilteringOptions
        .filter { it.isSelected }
        .map { it.id }
        .toTypedArray()

    fun toggleFilterOptionSelection(filterId: Long): ConferenceFilterUiState = copy(
        conferenceStatusFilteringOptions = conferenceStatusFilteringOptions.map { filter ->
            when (filter.id) {
                filterId -> filter.toggleSelection()
                else -> filter
            }
        },
        conferenceTagFilteringOptions = conferenceTagFilteringOptions.map { filter ->
            when (filter.id) {
                filterId -> filter.toggleSelection()
                else -> filter
            }
        },
    )

    fun resetSelection(): ConferenceFilterUiState = copy(
        conferenceStatusFilteringOptions = conferenceStatusFilteringOptions.map { status ->
            status.copy(isSelected = false)
        },
        conferenceTagFilteringOptions = conferenceTagFilteringOptions.map { tag ->
            tag.copy(
                isSelected = false,
            )
        },
        selectedStartDate = null,
        selectedEndDate = null,
    )
}
