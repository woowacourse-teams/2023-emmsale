package com.emmsale.presentation.ui.myCommentList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.model.Comment

class MyCommentsAdapter(
    private val onCommentClick: (feedId: Long, commentId: Long) -> Unit,
) : ListAdapter<Comment, MyCommentViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCommentViewHolder {
        return MyCommentViewHolder.create(parent, onCommentClick)
    }

    override fun onBindViewHolder(holder: MyCommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(
                oldItem: Comment,
                newItem: Comment,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Comment,
                newItem: Comment,
            ): Boolean = oldItem == newItem
        }
    }
}
