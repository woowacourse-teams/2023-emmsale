package com.emmsale.presentation.ui.childCommentList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.ui.childCommentList.uiState.CommentUiState

class ChildCommentAdapter(
    private val showProfile: (authorId: Long) -> Unit,
    private val editComment: (commentId: Long) -> Unit,
    private val deleteComment: (commentId: Long) -> Unit,
    private val reportComment: (commentId: Long) -> Unit,
) : ListAdapter<CommentUiState, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == PARENT_COMMENT_VIEW_TYPE) {
            ParentCommentViewHolder.create(
                parent,
                showProfile,
                editComment,
                deleteComment,
                reportComment,
            )
        } else {
            ChildCommentViewHolder.create(
                parent,
                showProfile,
                editComment,
                deleteComment,
                reportComment,
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ParentCommentViewHolder -> holder.bind(getItem(position))
            is ChildCommentViewHolder -> holder.bind(getItem(position))
        }
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
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: CommentUiState,
                newItem: CommentUiState,
            ): Boolean = oldItem == newItem
        }
    }
}
