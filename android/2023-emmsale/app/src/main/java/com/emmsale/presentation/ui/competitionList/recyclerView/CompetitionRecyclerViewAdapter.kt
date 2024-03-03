package com.emmsale.presentation.ui.competitionList.recyclerView

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.emmsale.model.Event
import com.emmsale.presentation.ui.conferenceList.recyclerView.EventDiffUtil

class CompetitionRecyclerViewAdapter(
    private val onClickEvent: (Event) -> Unit,
) : ListAdapter<Event, CompetitionViewHolder>(EventDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetitionViewHolder =
        CompetitionViewHolder(parent, onClickEvent)

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: CompetitionViewHolder, position: Int) {
        holder.bind(getItem(position))
        preload(holder.itemView.context, position)
    }

    private fun preload(context: Context, currentPosition: Int) {
        val endPosition = (currentPosition + PRELOAD_SIZE).coerceAtMost(currentList.size - 1)

        currentList
            .subList(currentPosition, endPosition)
            .forEach { event -> preload(context, event.posterImageUrl) }
    }

    private fun preload(context: Context, url: String?) {
        Glide.with(context)
            .load(url)
            .preload()
    }

    companion object {
        private const val PRELOAD_SIZE = 8
    }
}
