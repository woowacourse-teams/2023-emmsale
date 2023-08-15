package com.emmsale.presentation.ui.main.event

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emmsale.presentation.ui.main.event.competition.CompetitionFragment
import com.emmsale.presentation.ui.main.event.conference.ConferenceFragment

class EventFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = TAB_SIZE

    override fun createFragment(position: Int): Fragment = when (position) {
        CONFERENCE_FRAGMENT_POSITION -> ConferenceFragment()
        COMPETITION_FRAGMENT_POSITION -> CompetitionFragment()
        else -> throw IllegalArgumentException("올바르지 않은 fragment position 입니다.")
    }

    companion object {
        private const val TAB_SIZE = 2
        private const val CONFERENCE_FRAGMENT_POSITION = 0
        private const val COMPETITION_FRAGMENT_POSITION = 1
    }
}
