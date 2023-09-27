package com.emmsale.presentation.ui.generalPostList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.GeneralPost

class GeneralPostListAdapter(
    private val navigateToPostDetail: (postId: Long) -> Unit,
) : ListAdapter<GeneralPost, GeneralPostViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralPostViewHolder {
        return GeneralPostViewHolder.create(parent, navigateToPostDetail)
    }

    override fun onBindViewHolder(holder: GeneralPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<GeneralPost>() {
            override fun areItemsTheSame(
                oldItem: GeneralPost,
                newItem: GeneralPost,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: GeneralPost,
                newItem: GeneralPost,
            ): Boolean = oldItem.id == newItem.id
        }
    }
}
