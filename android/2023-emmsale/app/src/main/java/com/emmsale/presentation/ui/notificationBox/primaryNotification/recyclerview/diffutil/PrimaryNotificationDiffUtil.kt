package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

object PrimaryNotificationDiffUtil : DiffUtil.ItemCallback<PrimaryNotificationUiState>() {
    override fun areItemsTheSame(
        oldItem: PrimaryNotificationUiState,
        newItem: PrimaryNotificationUiState,
    ): Boolean = oldItem.notificationId == newItem.notificationId

    override fun areContentsTheSame(
        oldItem: PrimaryNotificationUiState,
        newItem: PrimaryNotificationUiState,
    ): Boolean = oldItem == newItem
}
