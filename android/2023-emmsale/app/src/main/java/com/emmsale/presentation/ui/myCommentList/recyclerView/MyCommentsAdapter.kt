package com.emmsale.presentation.ui.myCommentList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.myCommentList.uiState.MyCommentUiState

class MyCommentsAdapter(
    private val onClick: (eventId: Long, commentId: Long) -> Unit,
) : ListAdapter<MyCommentUiState, MyCommentViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCommentViewHolder {
        return MyCommentViewHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: MyCommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MyCommentUiState>() {
            override fun areItemsTheSame(
                oldItem: MyCommentUiState,
                newItem: MyCommentUiState,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: MyCommentUiState,
                newItem: MyCommentUiState,
            ): Boolean = oldItem == newItem
        }
    }
}
