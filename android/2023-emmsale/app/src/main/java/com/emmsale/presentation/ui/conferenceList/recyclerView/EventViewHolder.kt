package com.emmsale.presentation.ui.conferenceList.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.databinding.ItemEventBinding
import com.emmsale.model.Event

class EventViewHolder(
    parent: ViewGroup,
    onClickConference: (Event) -> Unit,
    onEventPosterPreDraw: (View) -> Unit,
) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false),
) {
    private val binding = ItemEventBinding.bind(itemView)

    init {
        binding.onClickConference = onClickConference
        binding.ivEventPoster.doOnPreDraw { onEventPosterPreDraw(it) }
    }

    fun bind(event: Event) {
        binding.event = event
    }
}
