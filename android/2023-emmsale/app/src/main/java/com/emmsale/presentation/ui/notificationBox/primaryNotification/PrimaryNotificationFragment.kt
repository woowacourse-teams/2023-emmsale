package com.emmsale.presentation.ui.notificationBox.primaryNotification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.emmsale.R
import com.emmsale.databinding.FragmentPrimaryNotificationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.eventdetail.comment.childComment.ChildCommentActivity
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.adapter.PastNotificationsHeaderAdapter
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.adapter.PrimaryNotificationsAdapter
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.adapter.RecentNotificationsHeaderAdapter
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationScreenUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationsUiEvent

class PrimaryNotificationFragment : BaseFragment<FragmentPrimaryNotificationBinding>() {
    override val layoutResId: Int = R.layout.fragment_primary_notification
    private val viewModel: PrimaryNotificationViewModel by viewModels {
        PrimaryNotificationViewModel.factory
    }

    private val recentNotificationsHeaderAdapter = RecentNotificationsHeaderAdapter()

    private val recentNotificationsAdapter = PrimaryNotificationsAdapter(
        readNotification = ::readNotification,
        showEvent = ::showEvent,
        showChildComments = ::showChildComments,
        deleteNotification = {},
    )

    private val pastNotificationsHeaderAdapter =
        PastNotificationsHeaderAdapter(::showNotificationDeleteConfirmDialog)

    private val pastNotificationsAdapter = PrimaryNotificationsAdapter(
        readNotification = {},
        showEvent = ::showEvent,
        showChildComments = ::showChildComments,
        deleteNotification = ::deleteNotification,
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
        binding.rvPrimarynotificationNotifications.apply {
            adapter = ConcatAdapter(
                recentNotificationsHeaderAdapter,
                recentNotificationsAdapter,
                pastNotificationsHeaderAdapter,
                pastNotificationsAdapter,
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
            if (it !is PrimaryNotificationScreenUiState.Success) return@observe
            recentNotificationsAdapter.submitList(it.recentNotifications)
            pastNotificationsAdapter.submitList(it.pastNotifications)
        }
    }

    private fun setupUiEvent() {
        viewModel.uiEvent.observe(viewLifecycleOwner) {
            handleUiEvent(it)
        }
    }

    private fun handleUiEvent(event: PrimaryNotificationsUiEvent?) {
        if (event == null) return
        when (event) {
            PrimaryNotificationsUiEvent.DELETE_ERROR -> binding.root.showSnackBar(R.string.primarynotification_delete_notification_failed_message)
        }
        viewModel.resetUiEvent()
    }
}
