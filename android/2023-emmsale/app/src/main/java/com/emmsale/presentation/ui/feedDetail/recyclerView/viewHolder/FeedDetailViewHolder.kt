package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemFeeddetailFeedDetailBinding
import com.emmsale.presentation.ui.feedDetail.recyclerView.FeedDetailImageItemDecoration
import com.emmsale.presentation.ui.feedDetail.recyclerView.FeedDetailImagesAdapter
import com.emmsale.presentation.ui.feedDetail.uiState.FeedDetailUiState

class FeedDetailViewHolder(
    private val binding: ItemFeeddetailFeedDetailBinding,
    onProfileImageClick: (authorId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private val imageUrlsAdapter: FeedDetailImagesAdapter = FeedDetailImagesAdapter()

    init {
        binding.onProfileImageClick = onProfileImageClick
        binding.rvFeeddetailFeedDetailImages.apply {
            adapter = imageUrlsAdapter
            itemAnimator = null
            addItemDecoration(FeedDetailImageItemDecoration())
        }
    }

    fun bind(feedDetailUiState: FeedDetailUiState) {
        binding.uiState = feedDetailUiState
        imageUrlsAdapter.submitList(feedDetailUiState.feedDetail.imageUrls)
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
