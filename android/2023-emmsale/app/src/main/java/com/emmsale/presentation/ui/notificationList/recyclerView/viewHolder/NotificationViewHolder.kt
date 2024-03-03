package com.emmsale.presentation.ui.notificationList.recyclerView.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.model.notification.Notification

abstract class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: Notification)
}
