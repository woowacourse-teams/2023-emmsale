package com.emmsale.presentation.ui.myFeedList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemMyFeedBinding
import com.emmsale.model.Feed

class MyFeedViewHolder(
    private val binding: ItemMyFeedBinding,
    navigateToDetail: (feedId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener { navigateToDetail(binding.feed!!.id) }
    }

    fun bind(feed: Feed) {
        binding.feed = feed
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (feedId: Long) -> Unit,
        ): MyFeedViewHolder {
            val binding =
                ItemMyFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyFeedViewHolder(binding, onItemClick)
        }
    }
}
