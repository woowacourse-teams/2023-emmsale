package com.emmsale.presentation.ui.eventSearch.recyclerView.eventSearchHistory

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.EventSearchHistory

class EventSearchHistoryAdapter(
    private val onHistoryClick: (eventSearch: EventSearchHistory) -> Unit,
    private val onDeleteClick: (eventSearch: EventSearchHistory) -> Unit,
) : ListAdapter<EventSearchHistory, EventSearchHistoryViewHolder>(diffUtil) {
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
        private val diffUtil = object : DiffUtil.ItemCallback<EventSearchHistory>() {
            override fun areItemsTheSame(
                oldItem: EventSearchHistory,
                newItem: EventSearchHistory,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: EventSearchHistory,
                newItem: EventSearchHistory,
            ): Boolean = oldItem == newItem
        }
    }
}
