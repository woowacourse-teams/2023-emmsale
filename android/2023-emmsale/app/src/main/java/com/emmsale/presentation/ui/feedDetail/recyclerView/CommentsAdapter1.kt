package com.emmsale.presentation.ui.feedDetail.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Comment
import com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder.CommentViewHolder1
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState

class CommentsAdapter1(
    private val onClick: (comment: Comment) -> Unit,
    private val onAuthorImageClick: (authorId: Long) -> Unit,
    private val onCommentMenuClick: (isWrittenByLoginUser: Boolean, commentId: Long) -> Unit,
) : ListAdapter<CommentUiState, CommentViewHolder1>(CommentDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder1 =
        CommentViewHolder1(
            parent = parent,
            onClick = onClick,
            onAuthorImageClick = onAuthorImageClick,
            onCommentMenuClick = onCommentMenuClick,
        )

    override fun onBindViewHolder(holder: CommentViewHolder1, position: Int) {
        holder.bind(getItem(position))
    }
}
