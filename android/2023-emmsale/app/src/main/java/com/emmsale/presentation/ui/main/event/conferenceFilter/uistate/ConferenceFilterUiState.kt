package com.emmsale.presentation.ui.main.event.conferenceFilter.uistate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConferenceFilterUiState(
    val id: Long,
    val name: String,
    var isSelected: Boolean = false,
) : Parcelable
