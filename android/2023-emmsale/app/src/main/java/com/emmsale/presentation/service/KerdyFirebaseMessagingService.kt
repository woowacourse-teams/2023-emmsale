package com.emmsale.presentation.service

import com.emmsale.R
import com.emmsale.data.comment.CommentService
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.ServiceFactory
import com.emmsale.data.common.handleApi
import com.emmsale.presentation.common.extension.showNotification
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.eventdetail.comment.childComment.ChildCommentActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class KerdyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        when {
            message.isFollowNotificationMessage() -> showFollowNotification(message)
            message.isChildCommentNotificationMessage() ->
                showChildCommentNotification(message)

            message.isInterestEventNotificationMessage() -> showInterestEventNotification(message)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun RemoteMessage.isFollowNotificationMessage(): Boolean {
        return data["senderName"] != null &&
            data["senderId"] != null &&
            data["receiverId"] != null &&
            data["openProfileUrl"] != null &&
            data["message"] != null
    }

    private fun showFollowNotification(message: RemoteMessage) {
        val senderName = message.data["senderName"] ?: return
        val senderId = message.data["senderId"] ?: return
        val receiverId = message.data["receiverId"] ?: return
        val openProfileUrl = message.data["openProfileUrl"] ?: return
        val notificationMessage = message.data["message"] ?: return

        baseContext.showNotification(
            title = getString(R.string.kerdyfirebasemessaging_follow_notification_title_format),
            message = getString(R.string.kerdyfirebasemessaging_follow_notification_message_format).format(
                senderName,
            ),
            channelId = FOLLOW_CHANNEL_ID,
            channelName = getString(R.string.kerdyfirebasemessaging_follow_notification_channel_name),
            channelDescription = getString(R.string.kerdyfirebasemessaging_follow_notification_channel_description),
        )
    }

    private fun RemoteMessage.isChildCommentNotificationMessage(): Boolean {
        return data["receiverId"] != null &&
            data["redirectId"] != null &&
            data["notificationType"] == "COMMENT" &&
            data["createdAt"] != null
    }

    private fun showChildCommentNotification(message: RemoteMessage) {
        fun getEventId(commentId: Long): Long {
            return runBlocking {
                val eventIdResult = handleApi(
                    execute = { commentService.getComment(commentId) },
                    mapToDomain = { it.parentComment.eventId },
                )
                when (eventIdResult) {
                    is ApiError -> -1
                    is ApiException -> -1
                    is ApiSuccess -> eventIdResult.data
                }
            }
        }

        val memberId = message.data["receiverId"]?.toLong() ?: return
        val parentCommentId = message.data["redirectId"]?.toLong() ?: return
        val notificationType = message.data["notificationType"] ?: return
        val createdAt = message.data["createdAt"] ?: return

        val eventId = getEventId(parentCommentId)
        if (eventId < 0) return

        baseContext.showNotification(
            title = getString(R.string.kerdyfirebasemessaging_child_comment_notification_title_format),
            message = createdAt.toTimeMessage(),
            channelId = CHILD_COMMENT_POSTING_CHANNEL_ID,
            channelName = getString(R.string.kerdyfirebasemessaging_child_comment_notification_channel_name),
            channelDescription = getString(R.string.kerdyfirebasemessaging_child_comment_notification_channel_description),
            intent = ChildCommentActivity.getIntent(this, eventId, parentCommentId),
        )
    }

    private fun RemoteMessage.isInterestEventNotificationMessage(): Boolean {
        return data["receiverId"] != null &&
            data["redirectId"] != null &&
            data["notificationType"] == "EVENT" &&
            data["createdAt"] != null
    }

    private fun showInterestEventNotification(message: RemoteMessage) {
        val memberId = message.data["receiverId"]?.toLong() ?: return
        val eventId = message.data["redirectId"]?.toLong() ?: return
        val notificationType = message.data["notificationType"] ?: return
        val createdAt = message.data["createdAt"] ?: return

        baseContext.showNotification(
            title = getString(R.string.kerdyfirebasemessaging_interest_event_notification_title_format),
            message = createdAt.toTimeMessage(),
            channelId = INTEREST_EVENT_CHANNEL_ID,
            channelName = getString(R.string.kerdyfirebasemessaging_interest_event_notification_channel_name),
            channelDescription = getString(R.string.kerdyfirebasemessaging_interest_event_notification_channel_description),
            intent = EventDetailActivity.getIntent(this, eventId),
        )
    }

    private fun String.toTimeMessage(): String {
        val dateTime = LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss"))
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(dateTime)
    }

    companion object {
        private const val FOLLOW_CHANNEL_ID = "follow_channel_id"
        private const val CHILD_COMMENT_POSTING_CHANNEL_ID = "child_comment_posting_channel_id"
        private const val INTEREST_EVENT_CHANNEL_ID = "interest_event_channel_id"

        private val commentService: CommentService =
            ServiceFactory().create(CommentService::class.java)
    }
}
