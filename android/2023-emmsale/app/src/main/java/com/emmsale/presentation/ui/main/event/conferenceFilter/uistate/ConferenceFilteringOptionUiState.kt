package com.emmsale.presentation.ui.main.event.conferenceFilter.uistate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConferenceFilteringOptionUiState(
    val id: Long,
    val name: String,
    val isSelected: Boolean = false,
) : Parcelable {
    fun toggleSelection(): ConferenceFilteringOptionUiState = copy(isSelected = !isSelected)
}
