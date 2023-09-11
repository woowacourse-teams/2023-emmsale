package com.emmsale.presentation.service

import com.emmsale.R
import com.emmsale.data.comment.CommentService
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.ServiceFactory
import com.emmsale.data.common.handleApi
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.extension.showNotification
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.eventdetail.comment.childComment.ChildCommentActivity
import com.emmsale.presentation.ui.notificationBox.NotificationBoxActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class KerdyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val configRepository = KerdyApplication.repositoryContainer.configRepository
        val isNotificationReceive = configRepository.getConfig().isNotificationReceive
        if (!isNotificationReceive) return

        when (message.data["notificationType"]) {
            FOLLOW_NOTIFICATION_TYPE -> {
                if (configRepository.getFollowNotificationReceiveConfig()) {
                    showFollowNotification(message)
                }
            }

            CHILD_COMMENT_NOTIFICATION_TYPE -> {
                if (configRepository.getChildCommentNotificationReceiveConfig()) {
                    showChildCommentNotification(message)
                }
            }

            EVENT_NOTIFICATION_TYPE -> {
                if (configRepository.getInterestEventNotificationReceiveConfig()) {
                    showInterestEventNotification(message)
                }
            }
        }
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
            channelId = R.id.id_all_follow_notification_channel,
            intent = NotificationBoxActivity.getIntent(this),
        )
    }

    private fun showChildCommentNotification(message: RemoteMessage) {
        fun getEventIdAndParentCommentId(commentId: Long): Pair<Long, Long> {
            return runBlocking {
                val eventIdResult = handleApi(
                    execute = { commentService.getComment(commentId) },
                    mapToDomain = { it.parentComment.eventId to it.parentComment.commentId },
                )
                when (eventIdResult) {
                    is ApiError -> ERROR_EVENT_ID to ERROR_EVENT_ID
                    is ApiException -> ERROR_EVENT_ID to ERROR_EVENT_ID
                    is ApiSuccess -> eventIdResult.data
                }
            }
        }

        val memberId = message.data["receiverId"]?.toLong() ?: return
        val childCommentId = message.data["redirectId"]?.toLong() ?: return
        val notificationType = message.data["notificationType"] ?: return
        val createdAt = message.data["createdAt"] ?: return

        val (eventId, parentCommentId) = getEventIdAndParentCommentId(childCommentId)
        if (eventId == ERROR_EVENT_ID) return

        baseContext.showNotification(
            title = getString(R.string.kerdyfirebasemessaging_child_comment_notification_title_format),
            message = createdAt.toTimeMessage(),
            channelId = R.id.id_all_child_comment_notification_channel,
            intent = ChildCommentActivity.getIntent(this, eventId, parentCommentId, true),
        )
    }

    private fun showInterestEventNotification(message: RemoteMessage) {
        val memberId = message.data["receiverId"]?.toLong() ?: return
        val eventId = message.data["redirectId"]?.toLong() ?: return
        val notificationType = message.data["notificationType"] ?: return
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

    companion object {
        private const val ERROR_EVENT_ID = -1L

        private const val FOLLOW_NOTIFICATION_TYPE = "REQUEST"
        private const val CHILD_COMMENT_NOTIFICATION_TYPE = "COMMENT"
        private const val EVENT_NOTIFICATION_TYPE = "EVENT"

        private val commentService: CommentService =
            ServiceFactory().create(CommentService::class.java)
    }
}
