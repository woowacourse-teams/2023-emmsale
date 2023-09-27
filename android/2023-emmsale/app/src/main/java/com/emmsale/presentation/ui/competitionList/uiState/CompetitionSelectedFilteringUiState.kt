package com.emmsale.presentation.ui.competitionList.uiState

data class CompetitionSelectedFilteringUiState(
    val competitionStatusFilteringOptions: List<CompetitionSelectedFilteringOptionUiState> = emptyList(),
    val competitionTagFilteringOptions: List<CompetitionSelectedFilteringOptionUiState> = emptyList(),
    val selectedStartDate: CompetitionSelectedFilteringDateOptionUiState? = null,
    val selectedEndDate: CompetitionSelectedFilteringDateOptionUiState? = null,
) {
    val selectedStatusFilteringOptionIds: Array<Long> = competitionStatusFilteringOptions
        .map { it.id }
        .toTypedArray()

    val selectedTagFilteringOptionIds: Array<Long> = competitionTagFilteringOptions
        .map { it.id }
        .toTypedArray()

    val isShowFilter: Boolean
        get() {
            if (selectedStartDate != null) return true
            if (selectedEndDate != null) return true
            return selectedStatusFilteringOptionIds.size + selectedTagFilteringOptionIds.size > 0
        }

    fun removeFilteringOptionBy(filterId: Long): CompetitionSelectedFilteringUiState = copy(
        competitionStatusFilteringOptions = competitionStatusFilteringOptions.filterNot { filterOption ->
            filterOption.id == filterId
        },
        competitionTagFilteringOptions = competitionTagFilteringOptions.filterNot { filterOption ->
            filterOption.id == filterId
        },
    )

    fun clearSelectedDate(): CompetitionSelectedFilteringUiState = copy(
        selectedStartDate = null,
        selectedEndDate = null,
    )
}
