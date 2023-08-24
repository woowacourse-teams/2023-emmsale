package com.emmsale.presentation.ui.notificationBox.primaryNotification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentPrimaryNotificationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.eventdetail.comment.childComment.ChildCommentActivity
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.PrimaryNotificationsAdapter
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationScreenUiState1

class PrimaryNotificationFragment : BaseFragment<FragmentPrimaryNotificationBinding>() {
    override val layoutResId: Int = R.layout.fragment_primary_notification
    private val viewModel: PrimaryNotificationViewModel1 by viewModels {
        PrimaryNotificationViewModel1.factory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataBinding()
        initRecyclerViews()
        setupUiState()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.deleteAllNotifications = ::showNotificationDeleteConfirmDialog
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

    private fun initRecyclerViews() {
        binding.rvPrimarynotificationRecentNotifications.apply {
            adapter = PrimaryNotificationsAdapter(
                readNotification = ::readNotification,
                showEvent = ::showEvent,
                showChildComments = ::showChildComments,
                deleteNotification = {},
            )
            itemAnimator = null
        }
        binding.rvPrimarynotificationPastNotifications.apply {
            adapter = PrimaryNotificationsAdapter(
                readNotification = {},
                showEvent = ::showEvent,
                showChildComments = ::showChildComments,
                deleteNotification = ::deleteNotification,
            )
            itemAnimator = null
        }
    }

    private fun readNotification(notificationId: Long) {
        viewModel.readNotification(notificationId)
    }

    private fun showEvent(eventId: Long) {
        EventDetailActivity.startActivity(requireContext(), eventId)
    }

    private fun showChildComments(eventId: Long, parentCommentId: Long) {
        ChildCommentActivity.startActivity(requireContext(), eventId, parentCommentId)
    }

    private fun deleteNotification(notificationId: Long) {
        viewModel.deleteNotification(notificationId)
    }

    private fun setupUiState() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            if (it !is PrimaryNotificationScreenUiState1.Success) return@observe
            val recentNotificationsAdapter =
                binding.rvPrimarynotificationRecentNotifications.adapter as PrimaryNotificationsAdapter
            recentNotificationsAdapter.submitList(it.recentNotifications)
            val pastNotificationsAdapter =
                binding.rvPrimarynotificationPastNotifications.adapter as PrimaryNotificationsAdapter
            pastNotificationsAdapter.submitList(it.pastNotifications)
        }
    }
}
