package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.ItemCommentNotificationBodyBinding
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.ChildCommentNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

class CommentNotificationViewHolder(
    parent: ViewGroup,
    onNotificationClick: (notification: PrimaryNotificationUiState) -> Unit = {},
    onDeleteClick: (notificationId: Long) -> Unit = {},
) : RecentNotificationViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_comment_notification_body,
        parent,
        false,
    ),
) {
    private val binding = ItemCommentNotificationBodyBinding.bind(itemView)

    init {
        binding.onNotificationClick = onNotificationClick
        binding.onDeleteClick = onDeleteClick
    }

    override fun bind(item: PrimaryNotificationUiState) {
        if (item !is ChildCommentNotificationUiState) return

        binding.commentNotification = item
    }
}
