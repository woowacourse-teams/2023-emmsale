package com.emmsale.presentation.ui.myFeedList.recyclerView

import androidx.recyclerview.widget.RecyclerView
import com.emmsale.data.model.Feed
import com.emmsale.databinding.ItemMyFeedBinding

class MyFeedViewHolder(
    private val binding: ItemMyFeedBinding,
    navigateToDetail: (feedId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            navigateToDetail(
                binding.feed!!.id,
            )
        }
    }

    fun bind(feed: Feed) {
        binding.feed = feed
    }
}
