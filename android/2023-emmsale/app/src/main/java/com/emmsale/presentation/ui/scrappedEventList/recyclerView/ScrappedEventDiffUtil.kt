package com.emmsale.presentation.ui.scrappedEventList.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.data.model.Event

object ScrappedEventDiffUtil : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(
        oldItem: Event,
        newItem: Event,
    ): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Event,
        newItem: Event,
    ): Boolean =
        oldItem == newItem
}
