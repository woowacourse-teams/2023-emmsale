package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.ItemCommentNotificationBodyBinding
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.ChildCommentNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

class ChildCommentNotificationViewHolder(
    parent: ViewGroup,
    onNotificationClick: (notification: PrimaryNotificationUiState) -> Unit = {},
    onDeleteNotificationClick: (notificationId: Long) -> Unit = {},
) : PrimaryNotificationViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_comment_notification_body,
        parent,
        false,
    ),
) {
    private val binding = ItemCommentNotificationBodyBinding.bind(itemView)

    init {
        binding.onNotificationClick = onNotificationClick
        binding.onDeleteClick = onDeleteNotificationClick
    }

    override fun bind(item: PrimaryNotificationUiState) {
        if (item !is ChildCommentNotificationUiState) return

        binding.commentNotification = item
    }
}
