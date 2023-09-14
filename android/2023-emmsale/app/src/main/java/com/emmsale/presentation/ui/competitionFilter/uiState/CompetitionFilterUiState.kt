package com.emmsale.presentation.ui.competitionFilter.uiState

data class CompetitionFilterUiState(
    val statusFilteringOptions: List<CompetitionFilteringOptionUiState> = emptyList(),
    val tagFilteringOptions: List<CompetitionFilteringOptionUiState> = emptyList(),
    val selectedStartDate: CompetitionFilteringDateOptionUiState? = null,
    val selectedEndDate: CompetitionFilteringDateOptionUiState? = null,
    val isLoading: Boolean = false,
    val isLoadingCompetitionFilterFailed: Boolean = false,
) {
    val selectedStatusFilteringOptionIds: Array<Long> = statusFilteringOptions
        .filter { it.isSelected }
        .map { it.id }
        .toTypedArray()

    val selectedTagFilteringOptionIds: Array<Long> = tagFilteringOptions
        .filter { it.isSelected }
        .map { it.id }
        .toTypedArray()

    fun toggleSelectionBy(filterId: Long): CompetitionFilterUiState = copy(
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

    fun resetSelection(): CompetitionFilterUiState = copy(
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
