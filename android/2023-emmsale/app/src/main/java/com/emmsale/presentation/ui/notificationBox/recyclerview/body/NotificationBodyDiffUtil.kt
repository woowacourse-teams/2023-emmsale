package com.emmsale.presentation.ui.notificationBox.recyclerview.body

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationBodyUiState

object NotificationBodyDiffUtil : DiffUtil.ItemCallback<NotificationBodyUiState>() {
    override fun areItemsTheSame(
        oldItem: NotificationBodyUiState,
        newItem: NotificationBodyUiState,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: NotificationBodyUiState,
        newItem: NotificationBodyUiState,
    ): Boolean = oldItem == newItem
}
