package com.emmsale.presentation.ui.conferenceList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.conferenceList.uiState.ConferenceUiState

class ConferenceRecyclerViewAdapter(
    private val onClickConference: (ConferenceUiState) -> Unit,
) : ListAdapter<ConferenceUiState, ConferenceViewHolder>(ConferenceDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConferenceViewHolder =
        ConferenceViewHolder(parent, onClickConference)

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: ConferenceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
