package com.emmsale.presentation.ui.notificationList.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ItemPastNotificationHeaderBinding

class PastNotificationsHeaderViewHolder(
    parent: ViewGroup,
    onDeleteAllNotificationClick: () -> Unit = {},
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_past_notification_header,
        parent,
        false,
    ),
) {
    private val binding = ItemPastNotificationHeaderBinding.bind(itemView)

    init {
        binding.onDeleteAllClick = onDeleteAllNotificationClick
    }
}
