package com.emmsale.presentation.ui.main.event.conference.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferencesUiState

object ConferenceDiffUtil : DiffUtil.ItemCallback<ConferencesUiState>() {
    override fun areItemsTheSame(
        oldItem: ConferencesUiState,
        newItem: ConferencesUiState,
    ): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: ConferencesUiState,
        newItem: ConferencesUiState,
    ): Boolean =
        oldItem == newItem
}
