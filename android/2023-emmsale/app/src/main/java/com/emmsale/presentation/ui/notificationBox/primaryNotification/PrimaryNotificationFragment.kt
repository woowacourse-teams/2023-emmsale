package com.emmsale.presentation.ui.notificationBox.primaryNotification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.emmsale.R
import com.emmsale.databinding.FragmentPrimaryNotificationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.PastNotificationAdapter
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.RecentNotificationAdapter

class PrimaryNotificationFragment : BaseFragment<FragmentPrimaryNotificationBinding>() {
    override val layoutResId: Int = R.layout.fragment_primary_notification
    private val viewModel: PrimaryNotificationViewModel by viewModels {
        PrimaryNotificationViewModel.factory
    }

    private val recentNotificationAdapter by lazy { RecentNotificationAdapter() }
    private val pastNotificationAdapter by lazy { PastNotificationAdapter() }
    private val primaryNotificationAdapter: ConcatAdapter by lazy {
        ConcatAdapter(
            recentNotificationAdapter,
            pastNotificationAdapter,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPrimaryNotification()
        setupObservers()
    }

    private fun initPrimaryNotification() {
        binding.rvPrimaryNotification.adapter = primaryNotificationAdapter
        binding.rvPrimaryNotification.setHasFixedSize(true)
        binding.rvPrimaryNotification.itemAnimator = null
    }

    private fun setupObservers() {
        setupRecentNotifications()
        setupPastNotifications()
    }

    private fun setupRecentNotifications() {
        viewModel.recentNotifications.observe(viewLifecycleOwner) { notifications ->
            recentNotificationAdapter.submitList(notifications.notifications)
        }
    }

    private fun setupPastNotifications() {
        viewModel.pastNotifications.observe(viewLifecycleOwner) { notifications ->
            pastNotificationAdapter.submitList(notifications.notifications)
        }
    }
}
