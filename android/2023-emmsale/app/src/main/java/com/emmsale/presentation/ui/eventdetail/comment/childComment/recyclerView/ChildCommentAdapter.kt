package com.emmsale.presentation.ui.eventdetail.comment.childComment.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState.CommentUiState

class ChildCommentAdapter(
    private val onCommentDelete: (Long) -> Unit,
) : ListAdapter<CommentUiState, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == PARENT_COMMENT_VIEW_TYPE) {
            ParentCommentViewHolder.create(parent, onCommentDelete)
        } else {
            ChildCommentViewHolder.create(parent, onCommentDelete)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ParentCommentViewHolder) {
            holder.bind(getItem(position))
            return
        }
        (holder as ChildCommentViewHolder).bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int =
        if (position == 0) PARENT_COMMENT_VIEW_TYPE else CHILD_COMMENT_VIEW_TYPE

    companion object {
        private const val PARENT_COMMENT_VIEW_TYPE = 1
        private const val CHILD_COMMENT_VIEW_TYPE = 2

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
