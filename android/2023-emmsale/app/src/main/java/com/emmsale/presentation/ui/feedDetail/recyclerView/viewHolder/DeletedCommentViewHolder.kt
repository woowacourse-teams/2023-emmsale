package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemAllDeletedCommentBinding
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState

class DeletedCommentViewHolder(
    private val binding: ItemAllDeletedCommentBinding,
    onClick: (commentId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onClick
    }

    fun bind(commentUiState: CommentUiState) {
        binding.uiState = commentUiState
    }

    companion object {
        fun from(parent: ViewGroup, onClick: (commentId: Long) -> Unit): DeletedCommentViewHolder {
            val binding = ItemAllDeletedCommentBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return DeletedCommentViewHolder(binding, onClick)
        }
    }
}
