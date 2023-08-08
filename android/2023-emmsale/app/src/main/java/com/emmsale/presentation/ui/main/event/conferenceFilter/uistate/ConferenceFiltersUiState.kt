package com.emmsale.presentation.ui.main.event.conferenceFilter.uistate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ConferenceFiltersUiState {
    @Parcelize
    data class Success(
        val statuses: List<ConferenceFilterUiState> = emptyList(),
        val tags: List<ConferenceFilterUiState> = emptyList(),
        var selectedStartDate: ConferenceFilterDateUiState? = null,
        var selectedEndDate: ConferenceFilterDateUiState? = null,
    ) : ConferenceFiltersUiState(), Parcelable {

        fun resetSelection(): Success = copy(
            statuses = statuses.map { status -> status.copy(isSelected = false) },
            tags = tags.map { tag -> tag.copy(isSelected = false) },
            selectedStartDate = null,
            selectedEndDate = null,
        )
    }

    object Loading : ConferenceFiltersUiState()
    object Error : ConferenceFiltersUiState()
}
