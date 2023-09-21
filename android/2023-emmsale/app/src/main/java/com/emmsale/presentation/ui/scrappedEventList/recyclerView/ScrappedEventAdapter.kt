package com.emmsale.presentation.ui.scrappedEventList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.scrappedEventList.uiState.ScrappedEventUiState

class ScrappedEventAdapter(
    private val onClickConference: (ScrappedEventUiState) -> Unit,
) : ListAdapter<ScrappedEventUiState, ScrappedEventViewHolder>(ScrappedEventDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScrappedEventViewHolder =
        ScrappedEventViewHolder(parent, onClickConference)

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: ScrappedEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}