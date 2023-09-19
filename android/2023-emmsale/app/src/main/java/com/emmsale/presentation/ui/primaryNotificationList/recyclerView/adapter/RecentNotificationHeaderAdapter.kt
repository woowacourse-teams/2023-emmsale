package com.emmsale.presentation.ui.primaryNotificationList.recyclerView.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.ui.primaryNotificationList.recyclerView.viewHolder.RecentNotificationsHeaderViewHolder

class RecentNotificationHeaderAdapter :
    RecyclerView.Adapter<RecentNotificationsHeaderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentNotificationsHeaderViewHolder = RecentNotificationsHeaderViewHolder(parent)

    override fun getItemCount(): Int = HEADER_COUNT

    override fun onBindViewHolder(holder: RecentNotificationsHeaderViewHolder, position: Int) {}

    override fun getItemViewType(position: Int): Int = VIEW_TYPE

    companion object {
        private const val HEADER_COUNT = 1
        private val VIEW_TYPE = RecentNotificationsHeaderViewHolder::class.java.hashCode()
    }
}
