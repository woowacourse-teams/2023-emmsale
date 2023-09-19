package com.emmsale.presentation.ui.eventPageList

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emmsale.presentation.ui.competitionList.CompetitionFragment
import com.emmsale.presentation.ui.conferenceList.ConferenceFragment
import com.emmsale.presentation.ui.scrappedEventList.ScrappedEventFragment

class EventFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = TAB_SIZE

    override fun createFragment(position: Int): Fragment = when (position) {
        SCRAP_FRAGMENT_POSITION -> ScrappedEventFragment()
        CONFERENCE_FRAGMENT_POSITION -> ConferenceFragment()
        COMPETITION_FRAGMENT_POSITION -> CompetitionFragment()
        else -> throw IllegalArgumentException("올바르지 않은 fragment position 입니다.")
    }

    companion object {
        private const val TAB_SIZE = 3
        private const val SCRAP_FRAGMENT_POSITION = 0
        private const val CONFERENCE_FRAGMENT_POSITION = 1
        private const val COMPETITION_FRAGMENT_POSITION = 2
    }
}
