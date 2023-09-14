package com.emmsale.presentation.ui.recruitmentNotificationList.recyclerView.header

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.recruitmentNotificationList.recyclerView.body.RecruitmentNotificationBodyClickListener
import com.emmsale.presentation.ui.recruitmentNotificationList.uiState.RecruitmentNotificationHeaderUiState

class RecruitmentNotificationHeaderAdapter(
    private val recruitmentNotificationHeaderClickListener: RecruitmentNotificationHeaderClickListener,
    private val recruitmentNotificationBodyClickListener: RecruitmentNotificationBodyClickListener,
) : ListAdapter<RecruitmentNotificationHeaderUiState, RecruitmentNotificationHeaderViewHolder>(
    RecruitmentNotificationHeaderDiffUtil,
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecruitmentNotificationHeaderViewHolder = RecruitmentNotificationHeaderViewHolder(
        parent,
        recruitmentNotificationHeaderClickListener,
        recruitmentNotificationBodyClickListener,
    )

    override fun onBindViewHolder(holder: RecruitmentNotificationHeaderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
