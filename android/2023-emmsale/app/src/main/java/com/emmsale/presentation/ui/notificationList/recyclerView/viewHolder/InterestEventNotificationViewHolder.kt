package com.emmsale.presentation.ui.notificationList.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.ItemInterestEventNotificationBinding
import com.emmsale.model.notification.InterestEventNotification
import com.emmsale.model.notification.Notification

class InterestEventNotificationViewHolder(
    parent: ViewGroup,
    onNotificationClick: (notification: Notification) -> Unit = {},
    onDeleteClick: (notificationId: Long) -> Unit = {},
) : NotificationViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_interest_event_notification,
        parent,
        false,
    ),
) {
    private val binding = ItemInterestEventNotificationBinding.bind(itemView)

    init {
        binding.onNotificationClick = onNotificationClick
        binding.onDeleteClick = onDeleteClick
    }

    override fun bind(item: Notification) {
        if (item !is InterestEventNotification) return

        binding.interestEventNotification = item
    }
}
