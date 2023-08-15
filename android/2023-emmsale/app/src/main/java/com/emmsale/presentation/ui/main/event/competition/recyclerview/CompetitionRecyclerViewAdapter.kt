package com.emmsale.presentation.ui.main.event.competition.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.main.event.competition.uistate.CompetitionUiState

class CompetitionRecyclerViewAdapter(
    private val onClickCompetition: (CompetitionUiState) -> Unit,
) : ListAdapter<CompetitionUiState, CompetitionViewHolder>(CompetitionDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetitionViewHolder =
        CompetitionViewHolder(parent, onClickCompetition)

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: CompetitionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
