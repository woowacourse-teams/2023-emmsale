package com.emmsale.presentation.ui.primaryNotificationList.recyclerView.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.ui.primaryNotificationList.recyclerView.viewHolder.PastNotificationsHeaderViewHolder

class PastNotificationHeaderAdapter(
    private val onDeleteAllNotificationClick: () -> Unit,
) : RecyclerView.Adapter<PastNotificationsHeaderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PastNotificationsHeaderViewHolder = PastNotificationsHeaderViewHolder(
        parent = parent,
        onDeleteAllNotificationClick = onDeleteAllNotificationClick,
    )

    override fun getItemCount(): Int = HEADER_COUNT

    override fun onBindViewHolder(holder: PastNotificationsHeaderViewHolder, position: Int) {}

    override fun getItemViewType(position: Int): Int = VIEW_TYPE

    companion object {
        private const val HEADER_COUNT = 1
        private val VIEW_TYPE = PastNotificationHeaderAdapter::class.java.hashCode()
    }
}
