package com.emmsale.presentation.ui.eventDetailInfo.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class EventInfoImageAdapter(
    private val imageUrls: List<String>,
) : RecyclerView.Adapter<EventInfoImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventInfoImageViewHolder {
        return EventInfoImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: EventInfoImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }
}
