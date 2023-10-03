package com.emmsale.presentation.ui.conferenceList.uiState

data class ConferenceSelectedFilteringUiState(
    val statusFilteringOptions: List<ConferenceSelectedFilteringOptionUiState> = emptyList(),
    val tagFilteringOptions: List<ConferenceSelectedFilteringOptionUiState> = emptyList(),
    val selectedStartDate: ConferenceSelectedFilteringDateOptionUiState? = null,
    val selectedEndDate: ConferenceSelectedFilteringDateOptionUiState? = null,
) {
    val selectedStatusFilteringOptionIds: Array<Long> = statusFilteringOptions
        .map { it.id }
        .toTypedArray()

    val selectedTagFilteringOptionIds: Array<Long> = tagFilteringOptions
        .map { it.id }
        .toTypedArray()

    val isShowFilter: Boolean
        get() {
            if (selectedStartDate != null) return true
            if (selectedEndDate != null) return true
            return selectedStatusFilteringOptionIds.size + selectedTagFilteringOptionIds.size > 0
        }

    fun removeFilteringOptionBy(filterId: Long): ConferenceSelectedFilteringUiState = copy(
        statusFilteringOptions = statusFilteringOptions.filterNot { filterOption ->
            filterOption.id == filterId
        },
        tagFilteringOptions = tagFilteringOptions.filterNot { filterOption ->
            filterOption.id == filterId
        },
    )

    fun clearSelectedDate(): ConferenceSelectedFilteringUiState = copy(
        selectedStartDate = null,
        selectedEndDate = null,
    )
}
