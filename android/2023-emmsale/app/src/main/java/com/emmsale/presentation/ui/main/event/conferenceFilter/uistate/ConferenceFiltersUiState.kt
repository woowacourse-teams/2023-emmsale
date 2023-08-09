package com.emmsale.presentation.ui.main.event.conferenceFilter.uistate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConferenceFiltersUiState(
    val conferenceStatusFilters: List<ConferenceFilterUiState> = emptyList(),
    val conferenceTagFilters: List<ConferenceFilterUiState> = emptyList(),
    val selectedStartDate: ConferenceFilterDateUiState? = null,
    val selectedEndDate: ConferenceFilterDateUiState? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) : Parcelable {
    fun toggleFilterSelection(filterId: Long): ConferenceFiltersUiState = copy(
        conferenceStatusFilters = conferenceStatusFilters.map { filter ->
            when (filter.id) {
                filterId -> filter.toggleSelection()
                else -> filter
            }
        },
        conferenceTagFilters = conferenceTagFilters.map { filter ->
            when (filter.id) {
                filterId -> filter.toggleSelection()
                else -> filter
            }
        },
    )

    fun resetSelection(): ConferenceFiltersUiState = copy(
        conferenceStatusFilters = conferenceStatusFilters.map { status ->
            status.copy(isSelected = false)
        },
        conferenceTagFilters = conferenceTagFilters.map { tag -> tag.copy(isSelected = false) },
        selectedStartDate = null,
        selectedEndDate = null,
    )
}
