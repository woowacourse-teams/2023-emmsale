package com.emmsale.presentation.ui.setting.myComments.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemMycommentsCommentBinding
import com.emmsale.presentation.ui.setting.myComments.uiState.MyCommentUiState

class MyCommentViewHolder(
    private val binding: ItemMycommentsCommentBinding,
    private val showChildComments: (eventId: Long, commentId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            showChildComments(
                binding.comment?.eventId ?: return@setOnClickListener,
                binding.comment?.id ?: return@setOnClickListener,
            )
        }
    }

    fun bind(comment: MyCommentUiState) {
        binding.comment = comment
    }

    companion object {
        fun create(
            parent: ViewGroup,
            showChildComments: (eventId: Long, commentId: Long) -> Unit,
        ): MyCommentViewHolder {
            val binding = ItemMycommentsCommentBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return MyCommentViewHolder(binding, showChildComments)
        }
    }
}
