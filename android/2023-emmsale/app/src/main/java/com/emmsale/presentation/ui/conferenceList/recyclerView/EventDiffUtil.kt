package com.emmsale.presentation.ui.conferenceList.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.model.Event

object EventDiffUtil : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(
        oldItem: Event,
        newItem: Event,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Event,
        newItem: Event,
    ): Boolean = oldItem == newItem
}
