package com.emmsale.presentation.ui.scrappedEventList.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.scrappedEventList.uiState.ScrappedEventUiState

object ScrappedEventDiffUtil : DiffUtil.ItemCallback<ScrappedEventUiState>() {
    override fun areItemsTheSame(
        oldItem: ScrappedEventUiState,
        newItem: ScrappedEventUiState,
    ): Boolean =
        oldItem.data.id == newItem.data.id

    override fun areContentsTheSame(
        oldItem: ScrappedEventUiState,
        newItem: ScrappedEventUiState,
    ): Boolean =
        oldItem == newItem
}
