package com.emmsale.presentation.ui.competitionList.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.databinding.ItemCompetitionBinding
import com.emmsale.presentation.common.views.EventTagChip
import com.emmsale.presentation.common.views.eventChipOf
import com.emmsale.presentation.ui.competitionList.uiState.CompetitionUiState

class CompetitionViewHolder(
    parent: ViewGroup,
    onClickCompetition: (CompetitionUiState) -> Unit,
) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_competition, parent, false),
) {
    private val binding = ItemCompetitionBinding.bind(itemView)

    init {
        binding.onClickCompetition = onClickCompetition
    }

    fun bind(event: CompetitionUiState) {
        binding.event = event
        binding.cgEventTags.removeAllViews()
        event.competition.tags.forEach(::addEventChip)
    }

    private fun addEventChip(tagName: String) {
        binding.cgEventTags.addView(createEventChip(itemView.context, tagName))
    }

    private fun createEventChip(context: Context, tagName: String): EventTagChip = context.eventChipOf {
        text = tagName
    }
}
