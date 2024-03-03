package com.emmsale.presentation.ui.eventSearch.recyclerView.eventSearchHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ItemEventSearchHistoryBinding
import com.emmsale.model.EventSearchHistory

class EventSearchHistoryViewHolder(
    parent: ViewGroup,
    onHistoryClick: (eventSearch: EventSearchHistory) -> Unit,
    onDeleteClick: (eventSearch: EventSearchHistory) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_event_search_history, parent, false),
) {
    private val binding = ItemEventSearchHistoryBinding.bind(itemView)

    init {
        binding.onHistoryClick = onHistoryClick
        binding.onDeleteClick = onDeleteClick
    }

    fun bind(eventSearch: EventSearchHistory) {
        binding.eventSearch = eventSearch
    }
}
