package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.header

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate.RecruitmentNotificationHeaderUiState

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
