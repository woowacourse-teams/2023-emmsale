package com.emmsale.presentation.ui.feedDetail.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState

class CommentsAdapter(
    private val onParentCommentClick: (parentCommentId: Long) -> Unit,
    private val onProfileImageClick: (authorId: Long) -> Unit,
    private val onCommentMenuClick: (isWrittenByLoginUser: Boolean, commentId: Long) -> Unit,
) : ListAdapter<CommentUiState, RecyclerView.ViewHolder>(CommentDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (CommentsViewType.of(viewType)) {
            CommentsViewType.COMMENT -> CommentViewHolder.from(
                parent = parent,
                onClick = onParentCommentClick,
                onProfileImageClick = onProfileImageClick,
                onCommentMenuClick = onCommentMenuClick,
            )

            CommentsViewType.CHILD_COMMENT -> ChildCommentViewHolder.from(
                parent = parent,
                onProfileImageClick = onProfileImageClick,
                onCommentMenuClick = onCommentMenuClick,
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CommentViewHolder) holder.bind(getItem(position))
        if (holder is ChildCommentViewHolder) holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).comment.parentId == null) {
            CommentsViewType.COMMENT.typeNumber
        } else {
            CommentsViewType.CHILD_COMMENT.typeNumber
        }
}
