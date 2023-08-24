package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder.PastNotificationsHeaderViewHolder

class PastNotificationsHeaderAdapter(
    private val deleteAllPastNotifications: () -> Unit,
) : RecyclerView.Adapter<PastNotificationsHeaderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PastNotificationsHeaderViewHolder = PastNotificationsHeaderViewHolder.create(
        parent = parent,
        deleteAllPastNotifications = deleteAllPastNotifications,
    )

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: PastNotificationsHeaderViewHolder, position: Int) {
    }
}
