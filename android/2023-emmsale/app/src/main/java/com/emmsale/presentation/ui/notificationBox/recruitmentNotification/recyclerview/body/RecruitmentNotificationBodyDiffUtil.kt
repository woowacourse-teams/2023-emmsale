package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.body

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate.RecruitmentNotificationBodyUiState

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
