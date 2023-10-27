package com.emmsale.presentation.service

import android.content.Intent
import android.net.Uri
import com.emmsale.R
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.repository.interfaces.ConfigRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.extension.addListener
import com.emmsale.presentation.common.extension.isUpdateNeeded
import com.emmsale.presentation.common.extension.showNotification
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.extension.topActivityName
import com.emmsale.presentation.ui.childCommentList.ChildCommentActivity
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.messageList.MessageListActivity
import com.emmsale.presentation.ui.splash.SplashActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class KerdyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var configRepository: ConfigRepository

    @Inject
    lateinit var commentRepository: CommentRepository

    @Inject
    lateinit var tokenRepository: TokenRepository

    @OptIn(DelicateCoroutinesApi::class)
    private val singleThreadCoroutine = newSingleThreadContext("KerdyFirebaseMessagingService")

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        checkAppUpdate(message)
    }

    private fun checkAppUpdate(message: RemoteMessage) {
        val appUpdateManager = AppUpdateManagerFactory.create(this)

        appUpdateManager.appUpdateInfo.addListener(
            onSuccess = { updateInfo ->
                CoroutineScope(singleThreadCoroutine).launch {
                    showNotification(message, updateInfo.isUpdateNeeded())
                }
            },
            onFailed = {
                showToast(R.string.splash_not_installed_playstore)
                navigateToPlayStore()
            },
        )
    }

    private fun showNotification(message: RemoteMessage, isUpdateNeeded: Boolean) {
        val config = configRepository.getConfig()
        val isNotificationReceive = config.isNotificationReceive
        if (!isNotificationReceive || tokenRepository.getToken() == null) return

        when (message.data["notificationType"]?.uppercase()) {
            CHILD_COMMENT_NOTIFICATION_TYPE -> {
                if (config.isCommentNotificationReceive) {
                    showChildCommentNotification(
                        message,
                        isUpdateNeeded,
                    )
                }
            }

            EVENT_NOTIFICATION_TYPE -> {
                if (config.isInterestEventNotificationReceive) {
                    showInterestEventNotification(
                        message,
                        isUpdateNeeded,
                    )
                }
            }

            MESSAGE_NOTIFICATION_TYPE -> {
                if (config.isMessageNotificationReceive) {
                    showMessageNotification(
                        message,
                        isUpdateNeeded,
                    )
                }
            }
        }
    }

    private fun navigateToPlayStore() {
        val intent = Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
        startActivity(intent)
    }

    private fun showChildCommentNotification(message: RemoteMessage, isUpdateNeeded: Boolean) {
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
        val content = message.data["content"] ?: return
        val writerName = message.data["writer"] ?: return
        val writerImageUrl = message.data["writerImageUrl"] ?: return

        val (feedId, parentCommentId) = getFeedIdAndParentCommentId(childCommentId)
        if (feedId == ERROR_FEED_ID) return

        val intent = if (isUpdateNeeded) {
            SplashActivity.getIntent(this)
        } else {
            ChildCommentActivity.getIntent(this, feedId, parentCommentId, childCommentId, false)
        }

        baseContext.showNotification(
            title = getString(R.string.kerdyfirebasemessaging_child_comment_notification_title),
            message = getString(R.string.kerdyfirebasemessaging_child_comment_notification_content_format).format(
                writerName,
                content,
            ),
            channelId = R.id.id_all_child_comment_notification_channel,
            intent = intent,
            largeIconUrl = writerImageUrl,
        )
    }

    private fun showInterestEventNotification(message: RemoteMessage, isUpdateNeeded: Boolean) {
        val eventId = message.data["redirectId"]?.toLong() ?: return
        val title = message.data["title"] ?: return

        val intent = if (isUpdateNeeded) {
            SplashActivity.getIntent(this)
        } else {
            EventDetailActivity.getIntent(this, eventId)
        }

        baseContext.showNotification(
            title = getString(R.string.kerdyfirebasemessaging_interest_event_notification_title_format),
            message = title,
            channelId = R.id.id_all_interest_event_notification_channel,
            intent = intent,
        )
    }

    private fun showMessageNotification(message: RemoteMessage, isUpdateNeeded: Boolean) {
        val roomId: String = message.data["roomId"] ?: return
        val senderId = message.data["senderId"]?.toLong() ?: return

        val senderProfileUrl = message.data["senderImageUrl"]
        val senderName = message.data["senderName"] ?: return
        val content = message.data["content"] ?: return

        val intent = if (isUpdateNeeded) {
            SplashActivity.getIntent(this)
        } else {
            MessageListActivity.getIntent(this, roomId, senderId)
        }

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
            notificationId = roomId.hashCode(),
            channelId = R.id.id_all_message_notification_channel,
            intent = intent,
            largeIconUrl = senderProfileUrl,
            groupKey = roomId,
        )
    }

    companion object {
        private const val ERROR_FEED_ID = -1L

        private const val CHILD_COMMENT_NOTIFICATION_TYPE = "COMMENT"
        private const val EVENT_NOTIFICATION_TYPE = "EVENT"
        private const val MESSAGE_NOTIFICATION_TYPE = "MESSAGE"
    }
}
