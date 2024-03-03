package com.emmsale.presentation.ui.feedList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemFeedBinding
import com.emmsale.model.Feed

class FeedViewHolder(
    private val binding: ItemFeedBinding,
    navigateToFeedDetail: (feedId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            navigateToFeedDetail(
                binding.feed?.id ?: return@setOnClickListener,
            )
        }
    }

    fun bind(feed: Feed) {
        binding.feed = feed
    }

    companion object {
        fun create(
            parent: ViewGroup,
            navigateToFeedDetail: (postId: Long) -> Unit,
        ): FeedViewHolder {
            val binding =
                ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FeedViewHolder(binding, navigateToFeedDetail)
        }
    }
}
