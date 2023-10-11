package com.emmsale.presentation.ui.eventSearch.recyclerView.eventSearchHistory

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.EventSearch

class EventSearchHistoryAdapter(
    private val onHistoryClick: (eventSearch: EventSearch) -> Unit,
    private val onDeleteClick: (eventSearch: EventSearch) -> Unit,
) : ListAdapter<EventSearch, EventSearchHistoryViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EventSearchHistoryViewHolder {
        return EventSearchHistoryViewHolder(parent, onHistoryClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: EventSearchHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<EventSearch>() {
            override fun areItemsTheSame(
                oldItem: EventSearch,
                newItem: EventSearch,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: EventSearch,
                newItem: EventSearch,
            ): Boolean = oldItem == newItem
        }
    }
}
