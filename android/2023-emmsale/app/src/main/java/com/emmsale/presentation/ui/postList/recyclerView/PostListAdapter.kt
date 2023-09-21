package com.emmsale.presentation.ui.postList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Post

class PostListAdapter(
    private val navigateToPostDetail: (postId: Long) -> Unit,
) : ListAdapter<Post, PostViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder.create(parent, navigateToPostDetail)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(
                oldItem: Post,
                newItem: Post,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: Post,
                newItem: Post,
            ): Boolean = oldItem.id == newItem.id
        }
    }
}
