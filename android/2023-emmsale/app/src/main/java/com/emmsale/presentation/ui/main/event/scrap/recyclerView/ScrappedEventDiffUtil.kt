package com.emmsale.presentation.ui.main.event.scrap.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.main.event.scrap.uistate.ScrappedEventUiState

object ScrappedEventDiffUtil : DiffUtil.ItemCallback<ScrappedEventUiState>() {
    override fun areItemsTheSame(
        oldItem: ScrappedEventUiState,
        newItem: ScrappedEventUiState,
    ): Boolean =
        oldItem.scrapId == newItem.scrapId

    override fun areContentsTheSame(
        oldItem: ScrappedEventUiState,
        newItem: ScrappedEventUiState,
    ): Boolean =
        oldItem == newItem
}
