package com.emmsale.presentation.ui.feedList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Feed

class FeedListAdapter(
    private val navigateToFeedDetail: (postId: Long) -> Unit,
) : ListAdapter<Feed, FeedViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder.create(parent, navigateToFeedDetail)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Feed>() {
            override fun areItemsTheSame(
                oldItem: Feed,
                newItem: Feed,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: Feed,
                newItem: Feed,
            ): Boolean = oldItem.id == newItem.id
        }
    }
}
