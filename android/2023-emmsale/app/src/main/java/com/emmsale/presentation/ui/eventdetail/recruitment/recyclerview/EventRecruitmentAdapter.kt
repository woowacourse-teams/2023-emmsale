package com.emmsale.presentation.ui.eventdetail.recruitment.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentPostUiState

class EventRecruitmentAdapter(
    private val showMemberProfile: (Long) -> Unit,
    private val navigateToDetail: (RecruitmentPostUiState) -> Unit,
) : ListAdapter<RecruitmentPostUiState, RecruitmentViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecruitmentViewHolder =
        RecruitmentViewHolder.create(parent, showMemberProfile, navigateToDetail)

    override fun onBindViewHolder(holder: RecruitmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<RecruitmentPostUiState>() {
            override fun areItemsTheSame(
                oldItem: RecruitmentPostUiState,
                newItem: RecruitmentPostUiState,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: RecruitmentPostUiState,
                newItem: RecruitmentPostUiState,
            ): Boolean = oldItem.id == newItem.id
        }
    }
}