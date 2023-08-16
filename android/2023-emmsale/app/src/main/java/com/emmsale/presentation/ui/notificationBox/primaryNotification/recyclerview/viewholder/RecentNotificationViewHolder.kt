package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

abstract class RecentNotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: PrimaryNotificationUiState)
}
