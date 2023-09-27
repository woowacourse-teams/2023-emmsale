package com.emmsale.presentation.ui.generalPostList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.data.model.GeneralPost
import com.emmsale.databinding.ItemPostBinding

class GeneralPostViewHolder(
    private val binding: ItemPostBinding,
    navigateToPostDetail: (postId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            navigateToPostDetail(
                binding.post?.id ?: return@setOnClickListener,
            )
        }
    }

    fun bind(generalPost: GeneralPost) {
        binding.post = generalPost
    }

    companion object {
        fun create(
            parent: ViewGroup,
            navigateToPostDetail: (postId: Long) -> Unit,
        ): GeneralPostViewHolder {
            val binding =
                ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return GeneralPostViewHolder(binding, navigateToPostDetail)
        }
    }
}
