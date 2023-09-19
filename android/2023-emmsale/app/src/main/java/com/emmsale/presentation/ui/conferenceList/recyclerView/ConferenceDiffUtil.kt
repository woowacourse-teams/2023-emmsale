package com.emmsale.presentation.ui.conferenceList.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.conferenceList.uiState.ConferenceUiState

object ConferenceDiffUtil : DiffUtil.ItemCallback<ConferenceUiState>() {
    override fun areItemsTheSame(
        oldItem: ConferenceUiState,
        newItem: ConferenceUiState,
    ): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: ConferenceUiState,
        newItem: ConferenceUiState,
    ): Boolean =
        oldItem == newItem
}
