package com.emmsale.presentation.ui.conferenceList.recyclerView

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.emmsale.data.model.Event

class ConferenceRecyclerViewAdapter(
    private val onClickConference: (Event) -> Unit,
) : ListAdapter<Event, ConferenceViewHolder>(EventDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ConferenceViewHolder = ConferenceViewHolder(parent, onClickConference)

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: ConferenceViewHolder, position: Int) {
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
