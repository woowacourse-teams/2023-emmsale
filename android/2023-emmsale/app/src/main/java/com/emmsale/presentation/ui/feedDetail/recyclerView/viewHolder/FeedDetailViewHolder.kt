package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemFeeddetailFeedDetailBinding
import com.emmsale.presentation.common.extension.dp
import com.emmsale.presentation.common.recyclerView.SpaceItemDecoration
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
            addItemDecoration(SpaceItemDecoration(width = IMAGE_INTERVAL))
        }
    }

    fun bind(feedDetailUiState: FeedDetailUiState) {
        binding.uiState = feedDetailUiState
        imageUrlsAdapter.submitList(feedDetailUiState.feedDetail.imageUrls)
    }

    companion object {
        private val IMAGE_INTERVAL: Int = 10.dp

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
