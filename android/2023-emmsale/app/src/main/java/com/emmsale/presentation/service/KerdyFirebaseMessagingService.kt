package com.emmsale.presentation.service

import com.emmsale.R
import com.emmsale.presentation.common.notification.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class KerdyFirebaseMessagingService : FirebaseMessagingService() {
    private val notificationHelper: NotificationHelper by lazy { NotificationHelper(this) }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val senderName = message.data["senderName"] ?: return
        val senderId = message.data["senderId"] ?: return
        val receiverId = message.data["receiverId"] ?: return
        val openProfileUrl = message.data["openProfileUrl"] ?: return
        val notificationMessage = message.data["message"] ?: return

        notificationHelper.showNotification(
            title = getString(R.string.follow_notification_title_format),
            message = getString(R.string.follow_notification_message_format).format(senderName),
            channelId = FOLLOW_CHANNEL_ID,
            channelName = getString(R.string.follow_notification_channel_name),
            channelDescription = getString(R.string.follow_notification_channel_description),
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    companion object {
        private const val FOLLOW_CHANNEL_ID = "follow_channel_id"
    }
}


