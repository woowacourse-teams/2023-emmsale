package com.emmsale.presentation.ui.myPostList.myRecruitmentPostList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.MyRecruitmentPost

class MyRecruitmentPostAdapter(
    private val navigateToDetail: (eventId: Long, recruitmentId: Long) -> Unit,
) : ListAdapter<MyRecruitmentPost, MyRecruitmentPostViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecruitmentPostViewHolder {
        return MyRecruitmentPostViewHolder.create(parent, navigateToDetail)
    }

    override fun onBindViewHolder(holder: MyRecruitmentPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MyRecruitmentPost>() {
            override fun areItemsTheSame(
                oldItem: MyRecruitmentPost,
                newItem: MyRecruitmentPost,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: MyRecruitmentPost,
                newItem: MyRecruitmentPost,
            ): Boolean = (oldItem.postId == newItem.postId && oldItem.eventId == newItem.eventId)
        }
    }
}
