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
    ) : ConferenceFiltersUiState(), Parcelable

    object Loading : ConferenceFiltersUiState()
    object Error : ConferenceFiltersUiState()
}
