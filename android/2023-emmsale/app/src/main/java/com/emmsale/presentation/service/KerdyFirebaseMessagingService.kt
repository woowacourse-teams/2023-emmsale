package com.emmsale.presentation.service

import android.content.Intent
import com.emmsale.R
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.repository.interfaces.ConfigRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.extension.showNotification
import com.emmsale.presentation.common.extension.topActivityName
import com.emmsale.presentation.ui.childCommentList.ChildCommentActivity
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.messageList.MessageListActivity
import com.emmsale.presentation.ui.notificationPageList.NotificationBoxActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class KerdyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var configRepository: ConfigRepository

    @Inject
    lateinit var commentRepository: CommentRepository

    @Inject
    lateinit var tokenRepository: TokenRepository

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val config = configRepository.getConfig()
        val isNotificationReceive = config.isNotificationReceive
        if (!isNotificationReceive || tokenRepository.getToken() == null) return

        when (message.data["notificationType"]) {
            FOLLOW_NOTIFICATION_TYPE -> {
                if (config.isFollowNotificationReceive) showFollowNotification(message)
            }

            CHILD_COMMENT_NOTIFICATION_TYPE -> {
                if (config.isCommentNotificationReceive) showChildCommentNotification(message)
            }

            EVENT_NOTIFICATION_TYPE -> {
                if (config.isInterestEventNotificationReceive) showInterestEventNotification(message)
            }

            MESSAGE_NOTIFICATION_TYPE -> {
                if (config.isMessageNotificationReceive) showMessageNotification(message)
            }
        }
    }

    private fun showFollowNotification(message: RemoteMessage) {
        val senderName = message.data["senderName"] ?: return

        baseContext.showNotification(
            title = getString(R.string.kerdyfirebasemessaging_follow_notification_title_format),
            message = getString(R.string.kerdyfirebasemessaging_follow_notification_message_format).format(
                senderName,
            ),
            channelId = R.id.id_all_follow_notification_channel,
            intent = NotificationBoxActivity.getIntent(this),
        )
    }

    private fun showChildCommentNotification(message: RemoteMessage) {
        fun getFeedIdAndParentCommentId(commentId: Long): Pair<Long, Long> {
            return runBlocking {
                when (val result = commentRepository.getComment(commentId)) {
                    is Failure, NetworkError -> ERROR_FEED_ID to ERROR_FEED_ID
                    is Success -> result.data.feedId to result.data.id
                    is Unexpected -> throw Throwable(result.error)
                }
            }
        }

        val childCommentId = message.data["redirectId"]?.toLong() ?: return
        val createdAt = message.data["createdAt"] ?: return

        val (feedId, parentCommentId) = getFeedIdAndParentCommentId(childCommentId)
        if (feedId == ERROR_FEED_ID) return

        baseContext.showNotification(
            title = getString(R.string.kerdyfirebasemessaging_child_comment_notification_title_format),
            message = createdAt.toTimeMessage(),
            channelId = R.id.id_all_child_comment_notification_channel,
            intent = ChildCommentActivity.getIntent(this, feedId, parentCommentId, true),
        )
    }

    private fun showInterestEventNotification(message: RemoteMessage) {
        val eventId = message.data["redirectId"]?.toLong() ?: return
        val createdAt = message.data["createdAt"] ?: return

        baseContext.showNotification(
            title = getString(R.string.kerdyfirebasemessaging_interest_event_notification_title_format),
            message = createdAt.toTimeMessage(),
            channelId = R.id.id_all_interest_event_notification_channel,
            intent = EventDetailActivity.getIntent(this, eventId),
        )
    }

    private fun String.toTimeMessage(): String {
        val dateTime = LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss"))
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(dateTime)
    }

    private fun showMessageNotification(message: RemoteMessage) {
        val roomId: String = message.data["roomId"] ?: return
        val senderId = message.data["senderId"]?.toLong() ?: return

        val senderProfileUrl = message.data["senderImageUrl"]
        val senderName = message.data["senderName"] ?: return
        val content = message.data["content"] ?: return

        if (topActivityName == MessageListActivity::class.java.name) {
            val messageIntent = MessageListActivity
                .getIntent(
                    applicationContext,
                    roomId,
                    senderId,
                    senderProfileUrl,
                    senderName,
                    content,
                )
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(messageIntent)
            return
        }

        baseContext.showNotification(
            title = senderName,
            message = content,
            channelId = R.id.id_all_message_notification_channel,
            intent = MessageListActivity.getIntent(this, roomId, senderId),
            largeIconUrl = senderProfileUrl,
            groupKey = roomId,
        )
    }

    companion object {
        private const val ERROR_FEED_ID = -1L

        private const val FOLLOW_NOTIFICATION_TYPE = "REQUEST"
        private const val CHILD_COMMENT_NOTIFICATION_TYPE = "COMMENT"
        private const val EVENT_NOTIFICATION_TYPE = "EVENT"
        private const val MESSAGE_NOTIFICATION_TYPE = "MESSAGE"
    }
}
