package com.emmsale.presentation.ui.notificationBox.recyclerview.header

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationHeaderUiState

object NotificationHeaderDiffUtil : DiffUtil.ItemCallback<NotificationHeaderUiState>() {
    override fun areItemsTheSame(
        oldItem: NotificationHeaderUiState,
        newItem: NotificationHeaderUiState,
    ): Boolean = oldItem.eventId == newItem.eventId

    override fun areContentsTheSame(
        oldItem: NotificationHeaderUiState,
        newItem: NotificationHeaderUiState,
    ): Boolean = oldItem == newItem
}
