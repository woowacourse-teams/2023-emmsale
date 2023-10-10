package com.emmsale.presentation.ui.competitionList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Event
import com.emmsale.presentation.ui.conferenceList.recyclerView.EventDiffUtil

class CompetitionRecyclerViewAdapter(
    private val onClickEvent: (Event) -> Unit,
) : ListAdapter<Event, CompetitionViewHolder>(EventDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetitionViewHolder =
        CompetitionViewHolder(parent, onClickEvent)

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: CompetitionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
