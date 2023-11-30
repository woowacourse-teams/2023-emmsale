package com.emmsale.presentation.ui.myRecruitmentList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.myRecruitmentList.uiState.MyRecruitmentUiState

class MyRecruitmentAdapter(
    private val navigateToDetail: (eventId: Long, recruitmentId: Long) -> Unit,
) : ListAdapter<MyRecruitmentUiState, MyRecruitmentViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecruitmentViewHolder {
        return MyRecruitmentViewHolder.create(parent, navigateToDetail)
    }

    override fun onBindViewHolder(holder: MyRecruitmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MyRecruitmentUiState>() {
            override fun areItemsTheSame(
                oldItem: MyRecruitmentUiState,
                newItem: MyRecruitmentUiState,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: MyRecruitmentUiState,
                newItem: MyRecruitmentUiState,
            ): Boolean = (oldItem.postId == newItem.postId && oldItem.eventId == newItem.eventId)
        }
    }
}
