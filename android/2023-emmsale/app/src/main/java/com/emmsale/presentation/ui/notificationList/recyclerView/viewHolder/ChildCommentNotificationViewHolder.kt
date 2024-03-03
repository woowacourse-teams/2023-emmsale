package com.emmsale.presentation.ui.notificationList.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.model.notification.ChildCommentNotification
import com.emmsale.model.notification.Notification
import com.emmsale.databinding.ItemCommentNotificationBodyBinding

class ChildCommentNotificationViewHolder(
    parent: ViewGroup,
    onNotificationClick: (notification: Notification) -> Unit = {},
    onDeleteNotificationClick: (notificationId: Long) -> Unit = {},
) : NotificationViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_comment_notification_body,
        parent,
        false,
    ),
) {
    private val binding = ItemCommentNotificationBodyBinding.bind(itemView)

    init {
        binding.onNotificationClick = onNotificationClick
        binding.onDeleteClick = onDeleteNotificationClick
    }

    override fun bind(item: Notification) {
        if (item !is ChildCommentNotification) return

        binding.commentNotification = item
    }
}
