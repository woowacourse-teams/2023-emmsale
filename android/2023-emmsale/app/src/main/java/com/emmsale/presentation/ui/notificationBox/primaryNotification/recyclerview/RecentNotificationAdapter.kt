package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewholder.CommentNotificationViewHolder
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewholder.InterestEventNotificationViewHolder
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewholder.RecentNotificationHeaderViewHolder
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewholder.RecentNotificationViewHolder
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.CommentNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.InterestEventNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

class RecentNotificationAdapter :
    ListAdapter<PrimaryNotificationUiState, RecentNotificationViewHolder>(
        PrimaryNotificationDiffUtil,
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentNotificationViewHolder = when (PrimaryNotificationViewType.of(viewType)) {
        PrimaryNotificationViewType.HEADER -> RecentNotificationHeaderViewHolder(parent)
        PrimaryNotificationViewType.COMMENT -> CommentNotificationViewHolder(parent)
        PrimaryNotificationViewType.INTEREST_EVENT -> InterestEventNotificationViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecentNotificationViewHolder, position: Int) {
        if (position == HEADER_POSITION) return

        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = when {
        position == HEADER_POSITION -> PrimaryNotificationViewType.HEADER.viewType
        getItem(position) is CommentNotificationUiState -> PrimaryNotificationViewType.COMMENT.viewType
        getItem(position) is InterestEventNotificationUiState -> PrimaryNotificationViewType.INTEREST_EVENT.viewType
        else -> INVALID_VIEW_TYPE
    }

    companion object {
        private const val HEADER_POSITION = 0
        private const val INVALID_VIEW_TYPE = -1
    }
}