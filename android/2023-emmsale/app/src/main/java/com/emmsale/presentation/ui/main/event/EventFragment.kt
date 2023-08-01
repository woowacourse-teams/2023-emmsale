package com.emmsale.presentation.ui.main.event

import android.os.Bundle
import android.view.View
import com.emmsale.R
import com.emmsale.databinding.FragmentEventBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class EventFragment : BaseFragment<FragmentEventBinding>() {
    override val layoutResId: Int = R.layout.fragment_event

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEventViewPager()
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

    companion object {
        val TAG: String = EventFragment::class.java.simpleName
    }
}
