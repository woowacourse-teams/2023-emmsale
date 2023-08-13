package com.emmsale.presentation.ui.main.event.conference.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferenceUiState

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
