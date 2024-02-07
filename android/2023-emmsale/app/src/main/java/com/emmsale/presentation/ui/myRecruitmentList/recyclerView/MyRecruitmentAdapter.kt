package com.emmsale.presentation.ui.myRecruitmentList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Recruitment

class MyRecruitmentAdapter(
    private val onItemClick: (eventId: Long, recruitmentId: Long) -> Unit,
) : ListAdapter<Recruitment, MyRecruitmentViewHolder>(
    object : DiffUtil.ItemCallback<Recruitment>() {
        override fun areItemsTheSame(
            oldItem: Recruitment,
            newItem: Recruitment,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: Recruitment,
            newItem: Recruitment,
        ): Boolean = (oldItem.id == newItem.id && oldItem.event.id == newItem.event.id)
    },
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecruitmentViewHolder {
        return MyRecruitmentViewHolder.create(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: MyRecruitmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
