package com.emmsale.presentation.ui.notificationList.recyclerView.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.model.notification.ChildCommentNotification
import com.emmsale.model.notification.InterestEventNotification
import com.emmsale.model.notification.Notification
import com.emmsale.presentation.ui.notificationList.recyclerView.diffutil.NotificationDiffUtil
import com.emmsale.presentation.ui.notificationList.recyclerView.viewHolder.ChildCommentNotificationViewHolder
import com.emmsale.presentation.ui.notificationList.recyclerView.viewHolder.InterestEventNotificationViewHolder
import com.emmsale.presentation.ui.notificationList.recyclerView.viewHolder.NotificationViewHolder
import com.emmsale.presentation.ui.notificationList.recyclerView.viewtype.NotificationBodyViewType

class NotificationsAdapter(
    private val onNotificationClick: (notification: Notification) -> Unit,
    private val onDeleteClick: (notificationId: Long) -> Unit,
) : ListAdapter<Notification, NotificationViewHolder>(
    NotificationDiffUtil,
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NotificationViewHolder = when (NotificationBodyViewType.of(viewType)) {
        NotificationBodyViewType.CHILD_COMMENT -> ChildCommentNotificationViewHolder(
            parent = parent,
            onNotificationClick = onNotificationClick,
            onDeleteNotificationClick = onDeleteClick,
        )

        NotificationBodyViewType.INTEREST_EVENT -> InterestEventNotificationViewHolder(
            parent = parent,
            onNotificationClick = onNotificationClick,
            onDeleteClick = onDeleteClick,
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = when {
        getItem(position) is ChildCommentNotification -> NotificationBodyViewType.CHILD_COMMENT.viewType
        getItem(position) is InterestEventNotification -> NotificationBodyViewType.INTEREST_EVENT.viewType
        else -> throw IllegalArgumentException("올바르지 않은 ViewType 입니다.")
    }
}
