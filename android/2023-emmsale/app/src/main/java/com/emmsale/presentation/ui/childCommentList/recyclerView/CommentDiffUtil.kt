package com.emmsale.presentation.ui.childCommentList.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState

object CommentDiffUtil : DiffUtil.ItemCallback<CommentUiState>() {
    override fun areItemsTheSame(oldItem: CommentUiState, newItem: CommentUiState): Boolean =
        oldItem.comment.id == newItem.comment.id

    override fun areContentsTheSame(oldItem: CommentUiState, newItem: CommentUiState): Boolean =
        oldItem == newItem
}
