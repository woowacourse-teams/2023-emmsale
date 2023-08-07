package com.emmsale.presentation.ui.notificationBox.recyclerview.header

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ItemNotificationHeaderBinding
import com.emmsale.presentation.ui.notificationBox.recyclerview.body.NotificationBodyClickListener
import com.emmsale.presentation.ui.notificationBox.recyclerview.body.NotificationBoxBodyAdapter
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationHeaderUiState

class NotificationHeaderViewHolder(
    parent: ViewGroup,
    notificationHeaderClickListener: NotificationHeaderClickListener,
    notificationBodyClickListener: NotificationBodyClickListener,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_notification_header, parent, false),
) {
    private val binding = ItemNotificationHeaderBinding.bind(itemView)
    private val notificationBoxBodyAdapter =
        NotificationBoxBodyAdapter(notificationBodyClickListener)

    init {
        binding.notificationHeaderClickListener = notificationHeaderClickListener
    }

    fun bind(header: NotificationHeaderUiState) {
        binding.header = header
        binding.rvNotificationBody.adapter = notificationBoxBodyAdapter
        notificationBoxBodyAdapter.submitList(header.notifications)
    }
}
