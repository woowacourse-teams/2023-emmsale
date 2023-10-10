package com.emmsale.presentation.ui.eventSearch.recyclerView.eventSearchHistory

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Event
import com.emmsale.presentation.ui.conferenceList.recyclerView.EventDiffUtil

class EventSearchHistoryAdapter(
    private val onHistoryClick: (event: Event) -> Unit,
    private val onDeleteClick: (event: Event) -> Unit,
) : ListAdapter<Event, EventSearchHistoryViewHolder>(EventDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EventSearchHistoryViewHolder {
        return EventSearchHistoryViewHolder(parent, onHistoryClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: EventSearchHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
