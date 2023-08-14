package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

object PrimaryNotificationDiffUtil : DiffUtil.ItemCallback<PrimaryNotificationUiState>() {
    override fun areItemsTheSame(
        oldItem: PrimaryNotificationUiState,
        newItem: PrimaryNotificationUiState,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: PrimaryNotificationUiState,
        newItem: PrimaryNotificationUiState,
    ): Boolean = oldItem == newItem
}
