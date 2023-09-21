package com.emmsale.presentation.ui.feedDetail.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder.FeedDetailImageViewHolder

class FeedDetailImagesAdapter(
    private val imageUrls: List<String>,
) : RecyclerView.Adapter<FeedDetailImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedDetailImageViewHolder =
        FeedDetailImageViewHolder.from(parent)

    override fun getItemCount(): Int = imageUrls.size

    override fun onBindViewHolder(holder: FeedDetailImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }
}
