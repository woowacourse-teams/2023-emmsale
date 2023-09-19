package com.emmsale.presentation.ui.competitionList.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.competitionList.uiState.CompetitionUiState

object CompetitionDiffUtil : DiffUtil.ItemCallback<CompetitionUiState>() {
    override fun areItemsTheSame(
        oldItem: CompetitionUiState,
        newItem: CompetitionUiState,
    ): Boolean = oldItem.competition.id == newItem.competition.id

    override fun areContentsTheSame(
        oldItem: CompetitionUiState,
        newItem: CompetitionUiState,
    ): Boolean = oldItem == newItem
}
