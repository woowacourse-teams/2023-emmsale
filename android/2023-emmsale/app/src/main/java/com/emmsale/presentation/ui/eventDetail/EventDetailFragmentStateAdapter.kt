package com.emmsale.presentation.ui.eventDetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailScreenUiState
import com.emmsale.presentation.ui.eventDetailInfo.EventInfoFragment
import com.emmsale.presentation.ui.feedList.FeedListFragment
import com.emmsale.presentation.ui.recruitmentList.RecruitmentsFragment

class EventDetailFragmentStateAdapter(
    fragmentActivity: FragmentActivity,
    private val eventId: Long,
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = EventDetailScreenUiState.values().size

    override fun createFragment(position: Int): Fragment {
        return when (EventDetailScreenUiState.from(position)) {
            EventDetailScreenUiState.INFORMATION -> EventInfoFragment.create()
            EventDetailScreenUiState.POST -> FeedListFragment.create(eventId)
            EventDetailScreenUiState.RECRUITMENT -> RecruitmentsFragment.create(eventId)
        }
    }
}
