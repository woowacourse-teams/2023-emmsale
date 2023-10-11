package com.emmsale.presentation.ui.eventSearch.recyclerView.eventSearchHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.data.model.EventSearch
import com.emmsale.databinding.ItemEventSearchHistoryBinding

class EventSearchHistoryViewHolder(
    parent: ViewGroup,
    onHistoryClick: (eventSearch: EventSearch) -> Unit,
    onDeleteClick: (eventSearch: EventSearch) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_event_search_history, parent, false),
) {
    private val binding = ItemEventSearchHistoryBinding.bind(itemView)

    init {
        binding.onHistoryClick = onHistoryClick
        binding.onDeleteClick = onDeleteClick
    }

    fun bind(eventSearch: EventSearch) {
        binding.eventSearch = eventSearch
    }
}
