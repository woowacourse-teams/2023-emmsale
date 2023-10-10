package com.emmsale.presentation.ui.eventSearch.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.data.model.Event
import com.emmsale.databinding.ItemConferenceBinding

class EventSearchViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_conference, parent, false),
) {
    private val binding = ItemConferenceBinding.bind(itemView)

    fun bind(event: Event) {
        binding.event = event
    }
}
