package com.emmsale.presentation.ui.notificationBox.primaryNotification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentPrimaryNotificationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.eventdetail.comment.childComment.ChildCommentActivity
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.ChildCommentNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.InterestEventNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationsUiEvent

class PrimaryNotificationFragment : BaseFragment<FragmentPrimaryNotificationBinding>() {
    override val layoutResId: Int = R.layout.fragment_primary_notification
    private val viewModel: PrimaryNotificationViewModel by viewModels {
        PrimaryNotificationViewModel.factory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupObservers()
    }

    private fun initView() {
        binding.viewModel = viewModel
    }

    private fun setupObservers() {
        setupUiEvent()
    }

    private fun setupUiEvent() {
        viewModel.event.observe(viewLifecycleOwner) {
            handleEvent(it)
        }
    }

    private fun handleEvent(event: PrimaryNotificationsUiEvent?) {
        if (event == null) return
        when (event) {
            PrimaryNotificationsUiEvent.DELETE_ERROR -> showNotificationDeleteConfirmDialog()
        }
        viewModel.resetEvent()
    }

    private fun showNotificationDeleteFailedMessage() {
        binding.root.showSnackBar(R.string.primarynotification_delete_notification_failed_message)
    }

    private fun navigateToDetail(notification: PrimaryNotificationUiState) {
        when (notification) {
            is InterestEventNotificationUiState -> navigateToEventScreen(notification.eventId)
            is ChildCommentNotificationUiState -> navigateToCommentScreen(
                eventId = notification.eventId,
                parentCommentId = notification.parentCommentId,
            )
        }
    }

    private fun navigateToEventScreen(eventId: Long) {
        EventDetailActivity.startActivity(requireContext(), eventId)
    }

    private fun navigateToCommentScreen(eventId: Long, parentCommentId: Long) {
        ChildCommentActivity.startActivity(requireContext(), eventId, parentCommentId)
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
