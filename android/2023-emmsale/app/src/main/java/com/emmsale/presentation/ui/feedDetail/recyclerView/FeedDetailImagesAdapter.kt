package com.emmsale.presentation.ui.feedDetail.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder.FeedDetailImageViewHolder

class FeedDetailImagesAdapter : ListAdapter<String, FeedDetailImageViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedDetailImageViewHolder =
        FeedDetailImageViewHolder.from(parent)

    override fun onBindViewHolder(holder: FeedDetailImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
        }
    }
}
