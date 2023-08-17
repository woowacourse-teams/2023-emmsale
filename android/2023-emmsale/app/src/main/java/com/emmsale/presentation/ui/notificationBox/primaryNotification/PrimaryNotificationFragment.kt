package com.emmsale.presentation.ui.notificationBox.primaryNotification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.emmsale.R
import com.emmsale.databinding.FragmentPrimaryNotificationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showSnackbar
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.eventdetail.comment.childComment.ChildCommentActivity
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.PastNotificationAdapter
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.RecentNotificationAdapter
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.CommentNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.InterestEventNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationsUiState

class PrimaryNotificationFragment : BaseFragment<FragmentPrimaryNotificationBinding>() {
    override val layoutResId: Int = R.layout.fragment_primary_notification
    private val viewModel: PrimaryNotificationViewModel by viewModels {
        PrimaryNotificationViewModel.factory
    }

    private val recentNotificationAdapter by lazy {
        RecentNotificationAdapter(
            onNotificationClick = { notification ->
                viewModel.changeToRead(notification.id)
                navigateToDetail(notification)
            },
        )
    }
    private val pastNotificationAdapter by lazy {
        PastNotificationAdapter(
            onNotificationClick = ::navigateToDetail,
            onDeleteClick = viewModel::deleteNotification,
            onDeleteAllClick = { showNotificationDeleteConfirmDialog() },
        )
    }
    private val primaryNotificationAdapter: ConcatAdapter by lazy {
        val config = ConcatAdapter.Config.Builder()
            .setIsolateViewTypes(false)
            .build()

        ConcatAdapter(
            config,
            recentNotificationAdapter,
            pastNotificationAdapter,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupObservers()
    }

    private fun initView() {
        binding.viewModel = viewModel
        initPrimaryNotificationRecyclerView()
    }

    private fun initPrimaryNotificationRecyclerView() {
        binding.rvPrimaryNotification.adapter = primaryNotificationAdapter
        binding.rvPrimaryNotification.setHasFixedSize(true)
        binding.rvPrimaryNotification.itemAnimator = null
    }

    private fun setupObservers() {
        setupRecentNotificationsObserver()
        setupPastNotificationsObserver()
    }

    private fun setupRecentNotificationsObserver() {
        viewModel.recentNotifications.observe(viewLifecycleOwner) { recentNotifications ->
            if (!recentNotifications.isLoading) {
                recentNotificationAdapter.submitList(recentNotifications.notifications)
            }
        }
    }

    private fun setupPastNotificationsObserver() {
        viewModel.pastNotifications.observe(viewLifecycleOwner) { pastNotifications ->
            handlePastNotificationsError(pastNotifications)
            when {
                !pastNotifications.isLoading -> pastNotificationAdapter.submitList(pastNotifications.notifications)
            }
        }
    }

    private fun handlePastNotificationsError(pastNotifications: PrimaryNotificationsUiState) {
        when {
            pastNotifications.isFetchingError -> showNotificationFetchingFailedMessage()
            pastNotifications.isDeleteNotificationError -> showNotificationDeleteFailedMessage()
        }
    }

    private fun showNotificationFetchingFailedMessage() {
        binding.root.showSnackbar(R.string.all_data_loading_failed_message)
    }

    private fun showNotificationDeleteFailedMessage() {
        binding.root.showSnackbar(R.string.primarynotification_delete_notification_failed_message)
    }

    private fun navigateToDetail(notification: PrimaryNotificationUiState) {
        when (notification) {
            is InterestEventNotificationUiState -> navigateToEventScreen(notification.redirectId)
            is CommentNotificationUiState -> navigateToCommentScreen(
                eventId = notification.eventId,
                commentId = notification.redirectId,
            )
        }
    }

    private fun navigateToEventScreen(eventId: Long) {
        EventDetailActivity.startActivity(requireContext(), eventId)
    }

    private fun navigateToCommentScreen(eventId: Long, commentId: Long) {
        ChildCommentActivity.startActivity(requireContext(), eventId, commentId)
    }

    private fun showNotificationDeleteConfirmDialog() {
        WarningDialog(
            context = context ?: return,
            title = getString(R.string.primarynotification_delete_notification_confirm_title),
            message = getString(R.string.primarynotification_delete_notification_confirm_message),
            positiveButtonLabel = getString(R.string.all_okay),
            negativeButtonLabel = getString(R.string.all_cancel),
            onPositiveButtonClick = { viewModel.deleteAllPastNotifications() },
        ).show()
    }
}
