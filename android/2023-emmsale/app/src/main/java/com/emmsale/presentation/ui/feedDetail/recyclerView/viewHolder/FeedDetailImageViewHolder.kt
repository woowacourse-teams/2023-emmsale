package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemFeeddetailFeedDetailImageBinding

class FeedDetailImageViewHolder(
    private val binding: ItemFeeddetailFeedDetailImageBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(imageUrl: String) {
        binding.imageUrl = imageUrl
    }

    companion object {
        fun from(parent: ViewGroup): FeedDetailImageViewHolder {
            val binding = ItemFeeddetailFeedDetailImageBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return FeedDetailImageViewHolder(binding)
        }
    }
}
