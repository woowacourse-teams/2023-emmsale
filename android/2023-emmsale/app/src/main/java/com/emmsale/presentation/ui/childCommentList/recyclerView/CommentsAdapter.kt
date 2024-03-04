package com.emmsale.presentation.ui.childCommentList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.model.Comment
import com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder.CommentViewHolder
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState

class CommentsAdapter(
    private val onCommentClick: (comment: Comment) -> Unit,
    private val onAuthorImageClick: (authorId: Long) -> Unit,
    private val onCommentMenuClick: (isWrittenByLoginUser: Boolean, comment: Comment) -> Unit,
) : ListAdapter<CommentUiState, CommentViewHolder>(CommentDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder =
        CommentViewHolder(
            parent = parent,
            onCommentClick = onCommentClick,
            onAuthorImageClick = onAuthorImageClick,
            onCommentMenuClick = onCommentMenuClick,
        )

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
