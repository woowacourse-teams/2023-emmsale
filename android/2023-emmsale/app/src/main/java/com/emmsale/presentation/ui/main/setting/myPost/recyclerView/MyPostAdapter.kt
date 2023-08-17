package com.emmsale.presentation.ui.main.setting.myPost.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.main.setting.myPost.uiState.MyPostUiState

class MyPostAdapter(
    private val navigateToDetail: (eventId: Long, recruitmentId: Long) -> Unit,
) : ListAdapter<MyPostUiState, MyPostViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        return MyPostViewHolder.create(parent, navigateToDetail)
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MyPostUiState>() {
            override fun areItemsTheSame(
                oldItem: MyPostUiState,
                newItem: MyPostUiState,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: MyPostUiState,
                newItem: MyPostUiState,
            ): Boolean = (oldItem.postId == newItem.postId && oldItem.eventId == oldItem.eventId)
        }
    }
}
