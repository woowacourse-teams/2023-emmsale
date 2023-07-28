package com.emmsale.presentation.ui.main.event.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.databinding.ItemEventBinding
import com.emmsale.presentation.ui.main.event.uistate.EventUiState

class EventViewHolder(
    parent: ViewGroup,
    onClickEvent: (EventUiState) -> Unit,
) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)) {
    private val binding: ItemEventBinding = ItemEventBinding.bind(itemView)

    init {
        binding.onClickEvent = onClickEvent
    }

    fun bind(event: EventUiState) {
        binding.event = event
    }
}
