package com.emmsale.presentation.ui.main.event

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emmsale.presentation.ui.main.event.conference.ConferenceFragment

class EventFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ConferenceFragment()
        1 -> ConferenceFragment()
        else -> throw IllegalArgumentException("올바르지 않은 fragment position 입니다.")
    }
}
