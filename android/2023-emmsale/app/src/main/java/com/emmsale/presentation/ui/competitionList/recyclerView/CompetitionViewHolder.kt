package com.emmsale.presentation.ui.competitionList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.databinding.ItemCompetitionBinding
import com.emmsale.model.Event

class CompetitionViewHolder(
    parent: ViewGroup,
    onClickCompetition: (Event) -> Unit,
) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_competition, parent, false),
) {
    private val binding = ItemCompetitionBinding.bind(itemView)

    init {
        binding.onClickCompetition = onClickCompetition
    }

    fun bind(event: Event) {
        binding.event = event
    }
}
