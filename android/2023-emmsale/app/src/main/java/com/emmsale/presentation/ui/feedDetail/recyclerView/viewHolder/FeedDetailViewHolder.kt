package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.ItemFeeddetailFeedDetailBinding
import com.emmsale.presentation.common.extension.dp
import com.emmsale.presentation.common.recyclerView.IntervalItemDecoration
import com.emmsale.presentation.ui.feedDetail.recyclerView.FeedDetailImagesAdapter
import com.emmsale.presentation.ui.feedDetail.uiState.FeedOrCommentUiState
import com.emmsale.presentation.ui.feedDetail.uiState.FeedUiState

class FeedDetailViewHolder(
    parent: ViewGroup,
    onAuthorImageClick: (authorId: Long) -> Unit,
) : FeedOrCommentViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_feeddetail_feed_detail, parent, false),
) {
    private val binding = ItemFeeddetailFeedDetailBinding.bind(itemView)

    private val imageUrlsAdapter: FeedDetailImagesAdapter = FeedDetailImagesAdapter()

    init {
        binding.onAuthorImageClick = onAuthorImageClick
        binding.rvFeeddetailFeedDetailImages.apply {
            adapter = imageUrlsAdapter
            itemAnimator = null
            addItemDecoration(IntervalItemDecoration(width = IMAGE_INTERVAL))
        }
    }

    override fun bind(uiState: FeedOrCommentUiState) {
        if (uiState !is FeedUiState) return
        binding.feed = uiState.feed
        imageUrlsAdapter.submitList(uiState.feed.imageUrls)
    }

    companion object {
        private val IMAGE_INTERVAL: Int = 10.dp
    }
}
