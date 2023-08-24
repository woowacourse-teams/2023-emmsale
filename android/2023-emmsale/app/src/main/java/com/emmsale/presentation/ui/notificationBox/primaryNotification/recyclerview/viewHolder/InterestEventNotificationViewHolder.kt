package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.databinding.ItemPrimarynotificationInterestEventNotificationBinding
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.InterestEventNotificationUiState

class InterestEventNotificationViewHolder(
    private val binding: ItemPrimarynotificationInterestEventNotificationBinding,
    private val readNotification: (notificationId: Long) -> Unit,
    private val showEvent: (eventId: Long) -> Unit,
    private val deleteNotification: (notificationId: Long) -> Unit,
) : PrimaryNotificationViewHolder(binding.root as ViewGroup) {

    init {
        binding.root.setOnClickListener {
            readNotification(binding.notification?.notificationId ?: return@setOnClickListener)
            showEvent(binding.notification?.eventId ?: return@setOnClickListener)
        }
        binding.ivInteresteventnotificationDeleteButton.setOnClickListener {
            deleteNotification(binding.notification?.notificationId ?: return@setOnClickListener)
        }
    }

    fun bind(notification: InterestEventNotificationUiState) {
        binding.notification = notification
    }

    companion object {
        fun create(
            parent: ViewGroup,
            readNotification: (notificationId: Long) -> Unit,
            showEvent: (eventId: Long) -> Unit,
            deleteNotification: (notificationId: Long) -> Unit,
        ): InterestEventNotificationViewHolder {
            val binding = ItemPrimarynotificationInterestEventNotificationBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return InterestEventNotificationViewHolder(
                binding = binding,
                readNotification = readNotification,
                showEvent = showEvent,
                deleteNotification = deleteNotification,
            )
        }
    }
}
