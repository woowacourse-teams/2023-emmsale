package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemFeeddetailChildCommentBinding
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState

class ChildCommentViewHolder(
    private val binding: ItemFeeddetailChildCommentBinding,
    onProfileImageClick: (authorId: Long) -> Unit,
    onCommentMenuClick: (isWrittenByLoginUser: Boolean, commentId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onProfileImageClick = onProfileImageClick
        binding.onCommentMenuClick = onCommentMenuClick
    }

    fun bind(uiState: CommentUiState) {
        binding.uiState = uiState
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onProfileImageClick: (authorId: Long) -> Unit,
            onCommentMenuClick: (isWrittenByLoginUser: Boolean, commentId: Long) -> Unit,
        ): ChildCommentViewHolder {
            val binding = ItemFeeddetailChildCommentBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return ChildCommentViewHolder(binding, onProfileImageClick, onCommentMenuClick)
        }
    }
}
