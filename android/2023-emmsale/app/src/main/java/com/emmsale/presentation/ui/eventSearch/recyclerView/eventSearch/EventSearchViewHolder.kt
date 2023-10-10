package com.emmsale.presentation.ui.eventSearch.recyclerView.eventSearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.data.model.Event
import com.emmsale.databinding.ItemConferenceBinding

class EventSearchViewHolder(
    parent: ViewGroup,
    onEventClick: (event: Event) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_conference, parent, false),
) {
    private val binding = ItemConferenceBinding.bind(itemView)

    init {
        binding.onClickConference = onEventClick
    }

    fun bind(event: Event) {
        binding.event = event
    }
}
