package com.emmsale.presentation.ui.notificationBox.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationUiState

object NotificationDiffUtil : DiffUtil.ItemCallback<NotificationUiState>() {
    override fun areItemsTheSame(
        oldItem: NotificationUiState,
        newItem: NotificationUiState,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: NotificationUiState,
        newItem: NotificationUiState,
    ): Boolean = oldItem == newItem
}
