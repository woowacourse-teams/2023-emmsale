package com.emmsale.presentation.ui.main.event.conference.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.databinding.ItemConferenceBinding
import com.emmsale.presentation.common.views.EventTag
import com.emmsale.presentation.common.views.eventChipOf
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferenceUiState

class ConferenceViewHolder(
    parent: ViewGroup,
    onClickConference: (ConferenceUiState) -> Unit,
) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_conference, parent, false),
) {
    private val binding = ItemConferenceBinding.bind(itemView)

    init {
        binding.onClickConference = onClickConference
    }

    fun bind(event: ConferenceUiState) {
        binding.event = event
        binding.cgEventTags.removeAllViews()
        event.tags.forEach(::addEventChip)
    }

    private fun addEventChip(tagName: String) {
        binding.cgEventTags.addView(createEventChip(itemView.context, tagName))
    }

    private fun createEventChip(context: Context, tagName: String): EventTag = context.eventChipOf {
        text = tagName
    }
}
