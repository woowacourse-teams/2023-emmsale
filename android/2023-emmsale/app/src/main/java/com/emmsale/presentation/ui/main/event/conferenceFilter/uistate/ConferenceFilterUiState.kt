package com.emmsale.presentation.ui.main.event.conferenceFilter.uistate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConferenceFilterUiState(
    val conferenceStatusFilteringOptions: List<ConferenceFilteringOptionUiState> = emptyList(),
    val conferenceTagFilteringOptions: List<ConferenceFilteringOptionUiState> = emptyList(),
    val selectedStartDate: ConferenceFilterDateOptionUiState? = null,
    val selectedEndDate: ConferenceFilterDateOptionUiState? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) : Parcelable {
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
        conferenceTagFilteringOptions = conferenceTagFilteringOptions.map { tag -> tag.copy(isSelected = false) },
        selectedStartDate = null,
        selectedEndDate = null,
    )
}
