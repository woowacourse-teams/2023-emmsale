package com.emmsale.presentation.ui.myFeedList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Feed

class MyFeedAdapter(
    private val onItemClick: (recruitmentId: Long) -> Unit,
) : ListAdapter<Feed, MyFeedViewHolder>(
    object : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(
            oldItem: Feed,
            newItem: Feed,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: Feed,
            newItem: Feed,
        ): Boolean = oldItem.id == newItem.id
    },
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFeedViewHolder {
        return MyFeedViewHolder.create(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: MyFeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
