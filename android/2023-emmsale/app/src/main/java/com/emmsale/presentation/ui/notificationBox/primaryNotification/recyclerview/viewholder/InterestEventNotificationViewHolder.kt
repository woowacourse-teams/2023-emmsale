package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.ItemInterestEventNotificationBodyBinding
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.InterestEventNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

class InterestEventNotificationViewHolder(
    parent: ViewGroup,
    onNotificationClick: (notification: PrimaryNotificationUiState) -> Unit = {},
    onDeleteClick: (notificationId: Long) -> Unit = {},
) : RecentNotificationViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_interest_event_notification_body,
        parent,
        false,
    ),
) {
    private val binding = ItemInterestEventNotificationBodyBinding.bind(itemView)

    init {
        binding.onNotificationClick = onNotificationClick
        binding.onDeleteClick = onDeleteClick
    }

    override fun bind(item: PrimaryNotificationUiState) {
        if (item !is InterestEventNotificationUiState) return

        binding.interestEventNotification = item
    }
}
