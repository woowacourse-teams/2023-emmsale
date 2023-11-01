package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.data.model.Comment
import com.emmsale.databinding.ItemAllCommentBinding
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState

class CommentViewHolder(
    parent: ViewGroup,
    onClick: (comment: Comment) -> Unit,
    onAuthorImageClick: (authorId: Long) -> Unit,
    onCommentMenuClick: (isWrittenByLoginUser: Boolean, commentId: Long) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_all_comment, parent, false),
) {
    private val binding: ItemAllCommentBinding = ItemAllCommentBinding.bind(itemView)

    init {
        binding.onClick = onClick
        binding.onAuthorImageClick = onAuthorImageClick
        binding.onCommentMenuClick = onCommentMenuClick
    }

    fun bind(commentUiState: CommentUiState) {
        binding.uiState = commentUiState
    }
}
