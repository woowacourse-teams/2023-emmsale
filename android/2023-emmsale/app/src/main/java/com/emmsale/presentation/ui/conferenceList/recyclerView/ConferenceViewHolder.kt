package com.emmsale.presentation.ui.conferenceList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.data.model.Event
import com.emmsale.databinding.ItemConferenceBinding

class ConferenceViewHolder(
    parent: ViewGroup,
    onClickConference: (Event) -> Unit,
) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_conference, parent, false),
) {
    private val binding = ItemConferenceBinding.bind(itemView)

    init {
        binding.onClickConference = onClickConference
    }

    fun bind(event: Event) {
        binding.event = event
    }
}
