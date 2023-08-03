package com.emmsale.presentation.ui.notificationBox.recyclerview.body

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ItemNotificationBodyBinding
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationBodyUiState

class NotificationBodyViewHolder(
    parent: ViewGroup,
    notificationBodyClickListener: NotificationBodyClickListener,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_notification_body, parent, false)
) {
    private val binding = ItemNotificationBodyBinding.bind(itemView)

    init {
        binding.notificationBodyClickListener = notificationBodyClickListener
    }

    fun bind(notification: NotificationBodyUiState) {
        binding.notification = notification
    }
}
