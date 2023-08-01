package com.emmsale.presentation.ui.main.event.conference

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class ConferenceRecyclerViewAdapter(
    private val onClickConference: (ConferencesUiState) -> Unit,
) : ListAdapter<ConferencesUiState, ConferenceViewHolder>(ConferenceDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConferenceViewHolder =
        ConferenceViewHolder(parent, onClickConference)

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: ConferenceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
