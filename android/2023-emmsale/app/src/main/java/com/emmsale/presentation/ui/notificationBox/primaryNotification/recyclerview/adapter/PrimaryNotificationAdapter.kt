package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.diffutil.PrimaryNotificationDiffUtil
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder.ChildCommentNotificationViewHolder
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder.InterestEventNotificationViewHolder
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder.PrimaryNotificationViewHolder
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewtype.PrimaryNotificationBodyViewType
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.ChildCommentNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.InterestEventNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

class PrimaryNotificationAdapter(
    private val onNotificationClick: (notification: PrimaryNotificationUiState) -> Unit,
    private val onDeleteClick: (notificationId: Long) -> Unit,
) : ListAdapter<PrimaryNotificationUiState, PrimaryNotificationViewHolder>(
    PrimaryNotificationDiffUtil,
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PrimaryNotificationViewHolder = when (PrimaryNotificationBodyViewType.of(viewType)) {
        PrimaryNotificationBodyViewType.CHILD_COMMENT -> ChildCommentNotificationViewHolder(
            parent = parent,
            onNotificationClick = onNotificationClick,
            onDeleteNotificationClick = onDeleteClick,
        )

        PrimaryNotificationBodyViewType.INTEREST_EVENT -> InterestEventNotificationViewHolder(
            parent = parent,
            onNotificationClick = onNotificationClick,
            onDeleteClick = onDeleteClick,
        )
    }

    override fun onBindViewHolder(holder: PrimaryNotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = when {
        getItem(position) is ChildCommentNotificationUiState -> PrimaryNotificationBodyViewType.CHILD_COMMENT.viewType
        getItem(position) is InterestEventNotificationUiState -> PrimaryNotificationBodyViewType.INTEREST_EVENT.viewType
        else -> throw IllegalArgumentException("올바르지 않은 ViewType 입니다.")
    }
}
