package com.emmsale.presentation.ui.eventPageList

import android.os.Bundle
import android.view.View
import com.emmsale.R
import com.emmsale.databinding.FragmentEventBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.ui.notificationPageList.NotificationBoxActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventFragment : BaseFragment<FragmentEventBinding>() {
    override val layoutResId: Int = R.layout.fragment_event

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        selectConferenceTab()
    }

    private fun initView() {
        selectConferenceTab()
        initEventViewPager()
        initNotificationButtonClickListener()
    }

    private fun selectConferenceTab() {
        binding.tlEvent.getTabAt(CONFERENCE_TAB).apply {
            this?.select()
        }
    }

    private fun initEventViewPager() {
        initEventFragmentStateAdapter()
        initEventTabLayoutMediator()
        initEventTabLayoutSelectedListener()
    }

    private fun initEventFragmentStateAdapter() {
        binding.vpEvent.adapter = EventFragmentStateAdapter(this)
    }

    private fun initEventTabLayoutMediator() {
        val eventTabNames = listOf(
            getString(R.string.event_scrap),
            getString(R.string.event_conference),
            getString(R.string.event_competition),
        )

        TabLayoutMediator(binding.tlEvent, binding.vpEvent) { tab, position ->
            tab.text = eventTabNames[position]
        }.attach()
    }

    private fun initEventTabLayoutSelectedListener() {
        binding.tlEvent.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.vpEvent.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun initNotificationButtonClickListener() {
        binding.tbEventToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.notification_button -> NotificationBoxActivity.startActivity(requireContext())
            }
            true
        }
    }

    companion object {
        const val TAG = "TAG_EVENT"
        private const val CONFERENCE_TAB = 1
    }
}
