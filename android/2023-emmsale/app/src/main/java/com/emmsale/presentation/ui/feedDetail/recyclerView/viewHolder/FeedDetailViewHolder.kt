package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemFeeddetailFeedDetailBinding
import com.emmsale.presentation.ui.feedDetail.uiState.FeedDetailUiState

class FeedDetailViewHolder(
    private val binding: ItemFeeddetailFeedDetailBinding,
    onProfileImageClick: (authorId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onProfileImageClick = onProfileImageClick
    }

    fun bind(feedDetailUiState: FeedDetailUiState) {
        binding.uiState = feedDetailUiState
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onProfileImageClick: (authorId: Long) -> Unit,
        ): FeedDetailViewHolder {
            val binding = ItemFeeddetailFeedDetailBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return FeedDetailViewHolder(binding, onProfileImageClick)
        }
    }
}
