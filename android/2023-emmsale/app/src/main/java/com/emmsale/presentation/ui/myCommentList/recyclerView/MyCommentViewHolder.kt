package com.emmsale.presentation.ui.myCommentList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemMycommentsCommentBinding
import com.emmsale.model.Comment

class MyCommentViewHolder(
    private val binding: ItemMycommentsCommentBinding,
    onCommentClick: (feedId: Long, commentId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onCommentClick = onCommentClick
    }

    fun bind(comment: Comment) {
        binding.comment = comment
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onCommentClick: (feedId: Long, commentId: Long) -> Unit,
        ): MyCommentViewHolder {
            val binding = ItemMycommentsCommentBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return MyCommentViewHolder(binding, onCommentClick)
        }
    }
}
