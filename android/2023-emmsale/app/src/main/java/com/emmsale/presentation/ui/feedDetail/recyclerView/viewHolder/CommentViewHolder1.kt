package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ItemAllComment1Binding
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState

class CommentViewHolder1(
    parent: ViewGroup,
    onClick: (commentId: Long) -> Unit,
    onAuthorImageClick: (authorId: Long) -> Unit,
    onCommentMenuClick: (isWrittenByLoginUser: Boolean, commentId: Long) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_all_comment1, parent, false),
) {
    private val binding: ItemAllComment1Binding = ItemAllComment1Binding.bind(itemView)

    init {
        binding.onClick = onClick
        binding.onAuthorImageClick = onAuthorImageClick
        binding.onCommentMenuClick = onCommentMenuClick
    }

    fun bind(commentUiState: CommentUiState) {
        binding.uiState = commentUiState
    }
}
