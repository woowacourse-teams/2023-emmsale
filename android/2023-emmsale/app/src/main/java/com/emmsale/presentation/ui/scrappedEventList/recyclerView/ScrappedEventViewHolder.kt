package com.emmsale.presentation.ui.scrappedEventList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.databinding.ItemScrappedEventBinding
import com.emmsale.presentation.ui.scrappedEventList.uiState.ScrappedEventUiState

class ScrappedEventViewHolder(
    parent: ViewGroup,
    onClickEvent: (ScrappedEventUiState) -> Unit,
) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_scrapped_event, parent, false),
) {
    private val binding = ItemScrappedEventBinding.bind(itemView)

    init {
        binding.onClickScrappedEvent = onClickEvent
    }

    fun bind(event: ScrappedEventUiState) {
        binding.event = event
    }
}
