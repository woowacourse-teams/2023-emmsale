package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.ItemPastNotificationHeaderBinding
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

class PastNotificationHeaderViewHolder(
    parent: ViewGroup,
    onDeleteAllClick: () -> Unit,
) : RecentNotificationViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_past_notification_header,
        parent,
        false,
    ),
) {
    private val binding = ItemPastNotificationHeaderBinding.bind(itemView)

    init {
        binding.onDeleteAllClick = onDeleteAllClick
    }

    override fun bind(item: PrimaryNotificationUiState) {
    }
}
