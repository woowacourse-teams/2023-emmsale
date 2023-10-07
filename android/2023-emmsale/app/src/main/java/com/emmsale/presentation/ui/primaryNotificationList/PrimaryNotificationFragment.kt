package com.emmsale.presentation.ui.primaryNotificationList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.emmsale.R
import com.emmsale.databinding.FragmentPrimaryNotificationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.childCommentList.ChildCommentActivity
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.primaryNotificationList.recyclerView.adapter.PastNotificationHeaderAdapter
import com.emmsale.presentation.ui.primaryNotificationList.recyclerView.adapter.PrimaryNotificationAdapter
import com.emmsale.presentation.ui.primaryNotificationList.recyclerView.adapter.RecentNotificationHeaderAdapter
import com.emmsale.presentation.ui.primaryNotificationList.uiState.ChildCommentNotificationUiState
import com.emmsale.presentation.ui.primaryNotificationList.uiState.InterestEventNotificationUiState
import com.emmsale.presentation.ui.primaryNotificationList.uiState.PrimaryNotificationScreenUiState
import com.emmsale.presentation.ui.primaryNotificationList.uiState.PrimaryNotificationUiState
import com.emmsale.presentation.ui.primaryNotificationList.uiState.PrimaryNotificationsUiEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrimaryNotificationFragment : BaseFragment<FragmentPrimaryNotificationBinding>() {
    override val layoutResId: Int = R.layout.fragment_primary_notification
    private val viewModel: PrimaryNotificationViewModel by viewModels()

    private val recentNotificationHeaderAdapter = RecentNotificationHeaderAdapter()
    private val recentNotificationAdapter = PrimaryNotificationAdapter(
        onNotificationClick = { notification ->
            readNotification(notification.notificationId)
            navigateToDetailScreen(notification)
        },
        onDeleteClick = ::deleteNotification,
    )

    private val pastNotificationHeaderAdapter = PastNotificationHeaderAdapter(
        onDeleteAllNotificationClick = ::showNotificationDeleteConfirmDialog,
    )
    private val pastNotificationAdapter = PrimaryNotificationAdapter(
        onNotificationClick = ::navigateToDetailScreen,
        onDeleteClick = ::deleteNotification,
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataBinding()
        initRecyclerView()
        setupUiState()
        setupUiEvent()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
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

    private fun initRecyclerView() {
        val concatAdapterConfig = ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build()

        binding.rvPrimarynotificationNotifications.apply {
            adapter = ConcatAdapter(
                concatAdapterConfig,
                recentNotificationHeaderAdapter,
                recentNotificationAdapter,
                pastNotificationHeaderAdapter,
                pastNotificationAdapter,
            )
            itemAnimator = null
            setHasFixedSize(false)
        }
    }

    private fun readNotification(notificationId: Long) {
        viewModel.readNotification(notificationId)
    }

    private fun navigateToDetailScreen(notification: PrimaryNotificationUiState) {
        when (notification) {
            is InterestEventNotificationUiState -> navigateToEventScreen(notification.eventId)
            is ChildCommentNotificationUiState -> navigateToCommentScreen(
                feedId = notification.feedId,
                parentCommentId = notification.parentCommentId,
            )
        }
    }

    private fun navigateToEventScreen(eventId: Long) {
        EventDetailActivity.startActivity(requireContext(), eventId)
    }

    private fun navigateToCommentScreen(feedId: Long, parentCommentId: Long) {
        ChildCommentActivity.startActivity(requireContext(), feedId, parentCommentId)
    }

    private fun deleteNotification(notificationId: Long) {
        viewModel.deleteNotification(notificationId)
    }

    private fun setupUiState() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            if (uiState !is PrimaryNotificationScreenUiState.Success) return@observe
            recentNotificationAdapter.submitList(uiState.recentNotifications.sortedByDescending { it.createdAt })
            pastNotificationAdapter.submitList(uiState.pastNotifications.sortedByDescending { it.createdAt })
        }
    }

    private fun setupUiEvent() {
        viewModel.uiEvent.observe(viewLifecycleOwner) {
            handleUiEvent(it)
        }
    }

    private fun handleUiEvent(event: Event<PrimaryNotificationsUiEvent>) {
        val content = event.getContentIfNotHandled() ?: return

        when (content) {
            PrimaryNotificationsUiEvent.NONE -> {}
            PrimaryNotificationsUiEvent.DELETE_FAIL -> binding.root.showSnackBar(R.string.primarynotification_delete_notification_failed_message)
        }
    }
}
