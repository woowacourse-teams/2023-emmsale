package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder.RecentNotificationsHeaderViewHolder

class RecentNotificationsHeaderAdapter :
    RecyclerView.Adapter<RecentNotificationsHeaderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentNotificationsHeaderViewHolder = RecentNotificationsHeaderViewHolder.create(parent)

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: RecentNotificationsHeaderViewHolder, position: Int) {
    }
}
