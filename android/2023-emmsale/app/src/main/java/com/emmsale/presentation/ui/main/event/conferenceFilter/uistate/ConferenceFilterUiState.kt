package com.emmsale.presentation.ui.main.event.conferenceFilter.uistate

data class ConferenceFilterUiState(
    val statusFilteringOptions: List<ConferenceFilteringOptionUiState> = emptyList(),
    val tagFilteringOptions: List<ConferenceFilteringOptionUiState> = emptyList(),
    val selectedStartDate: ConferenceFilteringDateOptionUiState? = null,
    val selectedEndDate: ConferenceFilteringDateOptionUiState? = null,
    val isLoading: Boolean = false,
    val isLoadingConferenceFilterFailed: Boolean = false,
) {
    val selectedStatusFilteringOptionIds: Array<Long> = statusFilteringOptions
        .filter { it.isSelected }
        .map { it.id }
        .toTypedArray()

    val selectedTagFilteringOptionIds: Array<Long> = tagFilteringOptions
        .filter { it.isSelected }
        .map { it.id }
        .toTypedArray()

    fun toggleSelectionBy(filterId: Long): ConferenceFilterUiState = copy(
        statusFilteringOptions = statusFilteringOptions.map { filter ->
            when (filter.id) {
                filterId -> filter.toggleSelection()
                else -> filter
            }
        },
        tagFilteringOptions = tagFilteringOptions.map { filter ->
            when (filter.id) {
                filterId -> filter.toggleSelection()
                else -> filter
            }
        },
    )

    fun resetSelection(): ConferenceFilterUiState = copy(
        statusFilteringOptions = statusFilteringOptions.map { status ->
            status.copy(isSelected = false)
        },
        tagFilteringOptions = tagFilteringOptions.map { tag ->
            tag.copy(
                isSelected = false,
            )
        },
        selectedStartDate = null,
        selectedEndDate = null,
    )
}
