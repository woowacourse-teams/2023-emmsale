package com.emmsale.presentation.ui.eventdetail.comment.childComment.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState.CommentUiState

class ChildCommentAdapter(
    private val deleteComment: (commentId: Long) -> Unit,
    private val showProfile: (authorId: Long) -> Unit,
) : ListAdapter<CommentUiState, ChildCommentViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildCommentViewHolder {
        return ChildCommentViewHolder.create(parent, deleteComment, showProfile)
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
