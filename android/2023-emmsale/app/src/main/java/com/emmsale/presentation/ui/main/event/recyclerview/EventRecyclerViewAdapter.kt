package com.emmsale.presentation.ui.main.event.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.main.event.uistate.EventUiState

class EventRecyclerViewAdapter(
    private val onClickEvent: (EventUiState) -> Unit
) : ListAdapter<EventUiState, EventViewHolder>(EventDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder =
        EventViewHolder(parent, onClickEvent)

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
