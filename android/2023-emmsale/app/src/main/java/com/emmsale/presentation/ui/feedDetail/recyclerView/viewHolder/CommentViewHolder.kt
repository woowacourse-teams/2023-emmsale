package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemAllCommentBinding
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState

class CommentViewHolder(
    private val binding: ItemAllCommentBinding,
    onClick: (commentId: Long) -> Unit,
    onProfileImageClick: (authorId: Long) -> Unit,
    onCommentMenuClick: (isWrittenByLoginUser: Boolean, commentId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onClick
        binding.onProfileImageClick = onProfileImageClick
        binding.onCommentMenuClick = onCommentMenuClick
    }

    fun bind(commentUiState: CommentUiState) {
        binding.uiState = commentUiState
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onClick: (commentId: Long) -> Unit,
            onProfileImageClick: (authorId: Long) -> Unit,
            onCommentMenuClick: (isWrittenByLoginUser: Boolean, commentId: Long) -> Unit,
        ): CommentViewHolder {
            val binding = ItemAllCommentBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return CommentViewHolder(binding, onClick, onProfileImageClick, onCommentMenuClick)
        }
    }
}