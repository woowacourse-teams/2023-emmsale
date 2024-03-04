package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.ItemAllCommentBinding
import com.emmsale.model.Comment
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState
import com.emmsale.presentation.ui.feedDetail.uiState.FeedOrCommentUiState

class CommentViewHolder(
    parent: ViewGroup,
    onCommentClick: (comment: Comment) -> Unit,
    onAuthorImageClick: (authorId: Long) -> Unit,
    onCommentMenuClick: (isWrittenByLoginUser: Boolean, comment: Comment) -> Unit,
) : FeedOrCommentViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_all_comment, parent, false),
) {
    private val binding = ItemAllCommentBinding.bind(itemView)

    init {
        binding.onCommentClick = onCommentClick
        binding.onAuthorImageClick = onAuthorImageClick
        binding.onCommentMenuClick = onCommentMenuClick
    }

    override fun bind(uiState: FeedOrCommentUiState) {
        if (uiState !is CommentUiState) return
        binding.uiState = uiState
    }
}
