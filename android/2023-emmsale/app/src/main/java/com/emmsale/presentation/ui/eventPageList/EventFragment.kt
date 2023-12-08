package com.emmsale.presentation.ui.eventPageList

import android.os.Bundle
import android.view.View
import com.emmsale.R
import com.emmsale.databinding.FragmentEventBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.ui.eventSearch.EventSearchActivity
import com.emmsale.presentation.ui.notificationList.NotificationsActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventFragment : BaseFragment<FragmentEventBinding>() {
    override val layoutResId: Int = R.layout.fragment_event

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectTab()
        setupEventSearchView()
        setupEventViewPager()
        setupNotificationView()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val selectedPosition = savedInstanceState?.getInt(KEY_TAB_POSITION) ?: CONFERENCE_TAB
        selectTab(selectedPosition)
    }

    private fun selectTab(position: Int = CONFERENCE_TAB) {
        binding.tlEvent.getTabAt(position)?.select()
    }

    private fun setupEventSearchView() {
        binding.btnEventSearch.setOnClickListener {
            EventSearchActivity.startActivity(requireContext())
        }
    }

    private fun setupEventViewPager() {
        setupEventFragmentStateAdapter()
        setupEventTabLayoutMediator()
        setupEventTabLayoutSelectedListener()
    }

    private fun setupEventFragmentStateAdapter() {
        binding.vpEvent.adapter = EventFragmentStateAdapter(this)
    }

    private fun setupEventTabLayoutMediator() {
        val eventTabNames = listOf(
            getString(R.string.event_scrap),
            getString(R.string.event_conference),
            getString(R.string.event_competition),
        )

        TabLayoutMediator(binding.tlEvent, binding.vpEvent) { tab, position ->
            tab.text = eventTabNames[position]
        }.attach()
    }

    private fun setupEventTabLayoutSelectedListener() {
        binding.tlEvent.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.vpEvent.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupNotificationView() {
        binding.tbEventToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.notification_button -> NotificationsActivity.startActivity(requireContext())
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_TAB_POSITION, binding.vpEvent.currentItem)
    }

    companion object {
        const val TAG = "TAG_EVENT"

        private const val KEY_TAB_POSITION = "key_tab_position"
        private const val CONFERENCE_TAB = 1
    }
}
