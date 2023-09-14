package com.emmsale.presentation.ui.recruitmentNotificationList.recyclerView.body

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.recruitmentNotificationList.uiState.RecruitmentNotificationBodyUiState

object RecruitmentNotificationBodyDiffUtil :
    DiffUtil.ItemCallback<RecruitmentNotificationBodyUiState>() {
    override fun areItemsTheSame(
        oldItem: RecruitmentNotificationBodyUiState,
        newItem: RecruitmentNotificationBodyUiState,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: RecruitmentNotificationBodyUiState,
        newItem: RecruitmentNotificationBodyUiState,
    ): Boolean = oldItem == newItem
}
