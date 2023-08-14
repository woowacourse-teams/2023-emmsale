package com.emmsale.presentation.ui.eventdetail.comment.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.eventdetail.comment.uiState.CommentUiState

class CommentsAdapter(
    private val showChildComments: (parentCommentId: Long) -> Unit,
    private val deleteComment: (commentId: Long) -> Unit,
) : ListAdapter<CommentUiState, CommentViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder.create(parent, showChildComments, deleteComment)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
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
