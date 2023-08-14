package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.ItemCommentNotificationBodyBinding
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.CommentNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

class CommentNotificationViewHolder(parent: ViewGroup) : RecentNotificationViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_comment_notification_body,
        parent,
        false,
    ),
) {
    private val binding = ItemCommentNotificationBodyBinding.bind(itemView)

    override fun bind(item: PrimaryNotificationUiState) {
        if (item !is CommentNotificationUiState) return

        binding.commentNotification = item
    }
}
