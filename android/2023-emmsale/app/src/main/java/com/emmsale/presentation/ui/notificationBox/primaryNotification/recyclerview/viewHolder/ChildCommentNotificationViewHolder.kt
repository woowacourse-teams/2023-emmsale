package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.databinding.ItemPrimarynotificationChildCommentNotificationBinding
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.ChildCommentNotificationUiState

class ChildCommentNotificationViewHolder(
    private val binding: ItemPrimarynotificationChildCommentNotificationBinding,
    private val readNotification: (notificationId: Long) -> Unit,
    private val showChildComments: (eventId: Long, parentCommentId: Long) -> Unit,
    private val deleteNotification: (notificationId: Long) -> Unit,
) : PrimaryNotificationViewHolder(binding.root as ViewGroup) {

    init {
        binding.root.setOnClickListener {
            readNotification(binding.notification?.notificationId ?: return@setOnClickListener)
            showChildComments(
                binding.notification?.eventId ?: return@setOnClickListener,
                binding.notification?.parentCommentId ?: return@setOnClickListener,
            )
        }
        binding.ivChildcommentnotificationDeleteButton.setOnClickListener {
            deleteNotification(binding.notification?.notificationId ?: return@setOnClickListener)
        }
    }

    fun bind(notification: ChildCommentNotificationUiState) {
        binding.notification = notification
    }

    companion object {
        fun create(
            parent: ViewGroup,
            readNotification: (notificationId: Long) -> Unit,
            showChildComments: (eventId: Long, parentCommentId: Long) -> Unit,
            deleteNotification: (notificationId: Long) -> Unit,
        ): ChildCommentNotificationViewHolder {
            val binding = ItemPrimarynotificationChildCommentNotificationBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return ChildCommentNotificationViewHolder(
                binding = binding,
                readNotification = readNotification,
                showChildComments = showChildComments,
                deleteNotification = deleteNotification,
            )
        }
    }
}
