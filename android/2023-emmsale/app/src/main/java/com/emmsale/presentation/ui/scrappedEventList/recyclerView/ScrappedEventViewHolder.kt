package com.emmsale.presentation.ui.scrappedEventList.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.databinding.ItemScrappedEventBinding
import com.emmsale.presentation.common.views.EventTag
import com.emmsale.presentation.common.views.eventChipOf
import com.emmsale.presentation.ui.scrappedEventList.uiState.ScrappedEventUiState

class ScrappedEventViewHolder(
    parent: ViewGroup,
    onClickEvent: (ScrappedEventUiState) -> Unit,
) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_scrapped_event, parent, false),
) {
    private val binding = ItemScrappedEventBinding.bind(itemView)

    init {
        binding.onClickEvent = onClickEvent
    }

    fun bind(event: ScrappedEventUiState) {
        binding.event = event
        binding.cgScrappedEventTags.removeAllViews()
        event.tags.forEach(::addEventChip)
    }

    private fun addEventChip(tagName: String) {
        binding.cgScrappedEventTags.addView(createEventChip(itemView.context, tagName))
    }

    private fun createEventChip(context: Context, tagName: String): EventTag = context.eventChipOf {
        text = tagName
    }
}
