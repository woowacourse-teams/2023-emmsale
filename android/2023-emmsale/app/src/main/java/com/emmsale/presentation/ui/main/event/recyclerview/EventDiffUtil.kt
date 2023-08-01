package com.emmsale.presentation.ui.main.event.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.main.event.uistate.EventUiState

object EventDiffUtil : DiffUtil.ItemCallback<EventUiState>() {
    override fun areItemsTheSame(oldItem: EventUiState, newItem: EventUiState): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: EventUiState, newItem: EventUiState): Boolean =
        oldItem == newItem
}
