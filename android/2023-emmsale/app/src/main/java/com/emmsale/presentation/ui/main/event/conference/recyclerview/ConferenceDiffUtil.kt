package com.emmsale.presentation.ui.main.event.conference.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferenceItemUiState

object ConferenceDiffUtil : DiffUtil.ItemCallback<ConferenceItemUiState>() {
    override fun areItemsTheSame(
        oldItem: ConferenceItemUiState,
        newItem: ConferenceItemUiState,
    ): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: ConferenceItemUiState,
        newItem: ConferenceItemUiState,
    ): Boolean =
        oldItem == newItem
}
