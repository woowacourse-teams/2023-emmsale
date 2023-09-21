package com.emmsale.presentation.ui.postList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.data.model.Post
import com.emmsale.databinding.ItemPostBinding

class PostViewHolder(
    private val binding: ItemPostBinding,
    navigateToPostDetail: (postId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            navigateToPostDetail(
                binding.post?.eventId ?: return@setOnClickListener,
            )
        }
    }

    fun bind(post: Post) {
        binding.post = post
    }

    companion object {
        fun create(
            parent: ViewGroup,
            navigateToPostDetail: (postId: Long) -> Unit,
        ): PostViewHolder {
            val binding =
                ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PostViewHolder(binding, navigateToPostDetail)
        }
    }
}
