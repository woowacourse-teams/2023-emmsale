package com.emmsale.presentation.ui.primaryNotificationList.recyclerView.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.ui.primaryNotificationList.uiState.PrimaryNotificationUiState

abstract class PrimaryNotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: PrimaryNotificationUiState)
}
