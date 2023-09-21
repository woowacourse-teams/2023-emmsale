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

    val selectedFilterSize: Int =
        competitionStatusFilteringOptions.size + competitionTagFilteringOptions.size

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