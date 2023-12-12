package com.emmsale.presentation.ui.feedDetail.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.feedDetail.uiState.FeedOrCommentUiState

object FeedAndCommentDiffUtil : DiffUtil.ItemCallback<FeedOrCommentUiState>() {
    override fun areItemsTheSame(
        oldItem: FeedOrCommentUiState,
        newItem: FeedOrCommentUiState,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: FeedOrCommentUiState,
        newItem: FeedOrCommentUiState,
    ): Boolean = oldItem == newItem
}
