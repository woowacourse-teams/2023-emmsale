package com.emmsale.presentation.ui.notificationList.recyclerView.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.data.model.notification.Notification

object NotificationDiffUtil : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(
        oldItem: Notification,
        newItem: Notification,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Notification,
        newItem: Notification,
    ): Boolean = oldItem == newItem
}
