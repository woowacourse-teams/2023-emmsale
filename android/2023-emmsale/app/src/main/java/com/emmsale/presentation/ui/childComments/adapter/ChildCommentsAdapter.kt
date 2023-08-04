package com.emmsale.presentation.ui.childComments.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.childComments.uiState.CommentUiState

class ChildCommentsAdapter(
    private val onDelete: (Long) -> Unit,
) : ListAdapter<CommentUiState, ChildCommentViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildCommentViewHolder {
        return ChildCommentViewHolder.create(parent, onDelete)
    }

    override fun onBindViewHolder(holder: ChildCommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<CommentUiState>() {
            override fun areItemsTheSame(
                oldItem: CommentUiState,
                newItem: CommentUiState,
            ): Boolean = oldItem.commentId == newItem.commentId

            override fun areContentsTheSame(
                oldItem: CommentUiState,
                newItem: CommentUiState,
            ): Boolean = oldItem == newItem

        }
    }
}