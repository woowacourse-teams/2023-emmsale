package com.emmsale.presentation.ui.scrappedEventList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.data.model.Event
import com.emmsale.databinding.ItemScrappedEventBinding

class ScrappedEventViewHolder(
    parent: ViewGroup,
    onClickEvent: (Event) -> Unit,
) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_scrapped_event, parent, false),
) {
    private val binding = ItemScrappedEventBinding.bind(itemView)

    init {
        binding.onClickScrappedEvent = onClickEvent
    }

    fun bind(event: Event) {
        binding.event = event
    }
}
