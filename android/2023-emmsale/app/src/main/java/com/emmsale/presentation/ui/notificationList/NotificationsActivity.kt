package com.emmsale.presentation.ui.notificationList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.emmsale.R
import com.emmsale.data.model.notification.ChildCommentNotification
import com.emmsale.data.model.notification.InterestEventNotification
import com.emmsale.data.model.notification.Notification
import com.emmsale.databinding.ActivityNotificationsBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.childCommentList.ChildCommentActivity
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.notificationList.recyclerView.adapter.NotificationsAdapter
import com.emmsale.presentation.ui.notificationList.recyclerView.adapter.PastNotificationHeaderAdapter
import com.emmsale.presentation.ui.notificationList.recyclerView.adapter.RecentNotificationHeaderAdapter
import com.emmsale.presentation.ui.notificationList.uiState.NotificationsUiEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsActivity :
    NetworkActivity<ActivityNotificationsBinding>(R.layout.activity_notifications) {

    override val viewModel: NotificationsViewModel by viewModels()

    private val recentNotificationHeaderAdapter = RecentNotificationHeaderAdapter()
    private val recentNotificationAdapter = NotificationsAdapter(
        onNotificationClick = { notification ->
            viewModel.readNotification(notification.id)
            navigateToDetailScreen(notification)
        },
        onDeleteClick = { viewModel.deleteNotification(it) },
    )

    private val pastNotificationHeaderAdapter = PastNotificationHeaderAdapter(
        onDeleteAllNotificationClick = ::showNotificationDeleteConfirmDialog,
    )
    private val pastNotificationAdapter = NotificationsAdapter(
        onNotificationClick = ::navigateToDetailScreen,
        onDeleteClick = { viewModel.deleteNotification(it) },
    )

    private fun navigateToDetailScreen(notification: Notification) {
        when (notification) {
            is ChildCommentNotification -> navigateToCommentScreen(
                feedId = notification.comment.feed.id,
                parentCommentId = notification.comment.parentCommentId!!,
                commentId = notification.comment.id,
            )

            is InterestEventNotification -> navigateToEventScreen(notification.event.id)
        }
    }

    private fun navigateToEventScreen(eventId: Long) {
        EventDetailActivity.startActivity(this, eventId)
    }

    private fun navigateToCommentScreen(feedId: Long, parentCommentId: Long, commentId: Long) {
        ChildCommentActivity.startActivity(
            context = this,
            feedId = feedId,
            parentCommentId = parentCommentId,
            highlightCommentId = commentId,
            fromPostDetail = false,
        )
    }

    private fun showNotificationDeleteConfirmDialog() {
        WarningDialog(
            context = this,
            title = getString(R.string.notifications_delete_notification_confirm_title),
            message = getString(R.string.notifications_delete_notification_confirm_message),
            positiveButtonLabel = getString(R.string.all_okay),
            negativeButtonLabel = getString(R.string.all_cancel),
            onPositiveButtonClick = { viewModel.deleteAllPastNotifications() },
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupDateBinding()
        setupToolbar()
        setupNotificationsRecyclerView()

        observeNotifications()
        observeUiEvent()
    }

    private fun setupDateBinding() {
        binding.viewModel = viewModel
    }

    private fun setupToolbar() {
        binding.tbNotifications.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupNotificationsRecyclerView() {
        val concatAdapterConfig = ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build()

        binding.rvNotifications.adapter = ConcatAdapter(
            concatAdapterConfig,
            recentNotificationHeaderAdapter,
            recentNotificationAdapter,
            pastNotificationHeaderAdapter,
            pastNotificationAdapter,
        )
    }

    private fun observeNotifications() {
        viewModel.notifications.observe(this) {
            recentNotificationAdapter.submitList(it.recentNotifications)
            pastNotificationAdapter.submitList(it.pastNotifications)
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: NotificationsUiEvent) {
        when (uiEvent) {
            NotificationsUiEvent.DeleteFail -> binding.root.showSnackBar(R.string.notifications_delete_notification_failed_message)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            Intent(context, NotificationsActivity::class.java).run { context.startActivity(this) }
        }
    }
}
