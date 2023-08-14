package com.emmsale.presentation.ui.notificationBox.primaryNotification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.emmsale.R
import com.emmsale.databinding.FragmentPrimaryNotificationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.PastNotificationAdapter
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.RecentNotificationAdapter
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.CommentNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.InterestEventNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

class PrimaryNotificationFragment : BaseFragment<FragmentPrimaryNotificationBinding>() {
    override val layoutResId: Int = R.layout.fragment_primary_notification
    private val viewModel: PrimaryNotificationViewModel by viewModels {
        PrimaryNotificationViewModel.factory
    }

    private val recentNotificationAdapter by lazy { RecentNotificationAdapter() }
    private val pastNotificationAdapter by lazy {
        PastNotificationAdapter(
            onNotificationClick = { notification ->
                viewModel.changeToRead(notification.id)
                navigateToDetail(notification)
            },
            onDeleteClick = { notificationId -> viewModel.deleteNotification(notificationId) },
            onDeleteAllClick = { viewModel.deleteAllPastNotifications() },
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
            when {
                recentNotifications.isFetchingError -> showToast(getString(R.string.all_data_loading_failed_message))
                !recentNotifications.isLoading ->
                    recentNotificationAdapter.submitList(recentNotifications.notifications)
            }
        }
    }

    private fun setupPastNotificationsObserver() {
        viewModel.pastNotifications.observe(viewLifecycleOwner) { pastNotifications ->
            when {
                !pastNotifications.isFetchingError && !pastNotifications.isLoading ->
                    pastNotificationAdapter.submitList(pastNotifications.notifications)
            }
        }
    }

    private fun navigateToDetail(notification: PrimaryNotificationUiState) {
        when (notification) {
            is InterestEventNotificationUiState -> navigateToEventScreen()
            is CommentNotificationUiState -> navigateToCommentScreen()
        }
    }

    private fun navigateToCommentScreen() {
    }

    private fun navigateToEventScreen() {
    }
}
