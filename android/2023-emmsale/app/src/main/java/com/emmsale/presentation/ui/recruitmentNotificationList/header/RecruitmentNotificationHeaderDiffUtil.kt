package com.emmsale.presentation.ui.recruitmentNotificationList.recyclerView.header

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.recruitmentNotificationList.uiState.RecruitmentNotificationHeaderUiState

object RecruitmentNotificationHeaderDiffUtil :
    DiffUtil.ItemCallback<RecruitmentNotificationHeaderUiState>() {
    override fun areItemsTheSame(
        oldItem: RecruitmentNotificationHeaderUiState,
        newItem: RecruitmentNotificationHeaderUiState,
    ): Boolean = oldItem.eventId == newItem.eventId

    override fun areContentsTheSame(
        oldItem: RecruitmentNotificationHeaderUiState,
        newItem: RecruitmentNotificationHeaderUiState,
    ): Boolean = oldItem == newItem
}
