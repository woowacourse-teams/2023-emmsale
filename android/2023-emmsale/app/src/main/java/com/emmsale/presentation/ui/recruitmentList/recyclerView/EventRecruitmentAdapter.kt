package com.emmsale.presentation.ui.recruitmentList.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Recruitment

class EventRecruitmentAdapter(
    private val navigateToDetail: (Recruitment) -> Unit,
) : ListAdapter<Recruitment, RecruitmentViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecruitmentViewHolder =
        RecruitmentViewHolder.create(parent, navigateToDetail)

    override fun onBindViewHolder(holder: RecruitmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Recruitment>() {
            override fun areItemsTheSame(
                oldItem: Recruitment,
                newItem: Recruitment,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: Recruitment,
                newItem: Recruitment,
            ): Boolean = oldItem.id == newItem.id
        }
    }
}
