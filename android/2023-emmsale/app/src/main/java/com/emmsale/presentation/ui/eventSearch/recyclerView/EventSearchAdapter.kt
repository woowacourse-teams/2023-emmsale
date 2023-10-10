package com.emmsale.presentation.ui.eventSearch.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Event
import com.emmsale.presentation.ui.conferenceList.recyclerView.EventDiffUtil

class EventSearchAdapter(
    private val onEventClick: (event: Event) -> Unit,
) : ListAdapter<Event, EventSearchViewHolder>(EventDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EventSearchViewHolder = EventSearchViewHolder(parent, onEventClick)

    override fun onBindViewHolder(holder: EventSearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
