package com.emmsale.presentation.ui.notificationBox.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ItemNotificationBodyBinding
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationUiState

class NotificationViewHolder(
    parent: ViewGroup,
    onClickNotification: (notificationId: Long, otherUid: Long) -> Unit,
    onDelete: (notificationId: Long) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_notification_body, parent, false)
) {
    private val binding = ItemNotificationBodyBinding.bind(itemView)

    init {
        binding.onClickNotification = onClickNotification
        binding.onDelete = onDelete
    }

    fun bind(notification: NotificationUiState) {
        binding.notification = notification
    }
}
