package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.ItemPrimaryNotificationBinding
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.InterestEventNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

class InterestEventNotificationViewHolder(
    parent: ViewGroup,
    onNotificationClick: (notification: PrimaryNotificationUiState) -> Unit = {},
    onDeleteClick: (notificationId: Long) -> Unit = {},
) : PrimaryNotificationViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_primary_notification,
        parent,
        false,
    ),
) {
    private val binding = ItemPrimaryNotificationBinding.bind(itemView)

    init {
        binding.onNotificationClick = onNotificationClick
        binding.onDeleteClick = onDeleteClick
    }

    override fun bind(item: PrimaryNotificationUiState) {
        if (item !is InterestEventNotificationUiState) return

        binding.interestEventNotification = item
    }
}
