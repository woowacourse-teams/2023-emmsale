package com.emmsale.presentation.ui.eventdetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailScreenUiState
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailScreenUiState.INFORMATION
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailScreenUiState.POST
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailScreenUiState.RECRUITMENT
import com.emmsale.presentation.ui.eventDetailInfo.EventInfoFragment
import com.emmsale.presentation.ui.postList.PostListFragment
import com.emmsale.presentation.ui.recruitmentList.EventRecruitmentFragment

class EventDetailFragmentStateAdapter(
    fragmentActivity: FragmentActivity,
    private val eventId: Long,
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = EventDetailScreenUiState.values().size

    override fun createFragment(position: Int): Fragment {
        return when (EventDetailScreenUiState.from(position)) {
            INFORMATION -> EventInfoFragment.create()
            POST -> PostListFragment.create(eventId)
            RECRUITMENT -> EventRecruitmentFragment.create(eventId)
        }
    }
}
