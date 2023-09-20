package com.emmsale.presentation.service

import com.emmsale.R
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.extension.showNotification
import com.emmsale.presentation.ui.childCommentList.ChildCommentActivity
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.notificationPageList.NotificationBoxActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class KerdyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val configRepository = KerdyApplication.repositoryContainer.configRepository
        val config = configRepository.getConfig()
        val isNotificationReceive = config.isNotificationReceive
        if (!isNotificationReceive) return

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
                val commentRepository = KerdyApplication.repositoryContainer.commentRepository
                when (val result = commentRepository.getComment(commentId)) {
                    is Failure, NetworkError -> ERROR_EVENT_ID to ERROR_EVENT_ID
                    is Success -> result.data.feedId to (
                        result.data.parentId
                            ?: throw IllegalArgumentException("대댓글만 알림을 받을 수 있습니다. 알림 메세지를 보내는 로직을 다시 확인해주세요.")
                        )

                    is Unexpected -> throw Throwable(result.error)
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
    }
}
