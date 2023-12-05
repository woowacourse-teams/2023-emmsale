package com.emmsale.presentation.ui.myCommentList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.data.model.Comment
import com.emmsale.databinding.ItemMycommentsCommentBinding

class MyCommentViewHolder(
    private val binding: ItemMycommentsCommentBinding,
    onClick: (eventId: Long, parentCommentId: Long, commentId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onClick
    }

    fun bind(comment: Comment) {
        binding.comment = comment
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onClick: (eventId: Long, parentCommentId: Long, commentId: Long) -> Unit,
        ): MyCommentViewHolder {
            val binding = ItemMycommentsCommentBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return MyCommentViewHolder(binding, onClick)
        }
    }
}
