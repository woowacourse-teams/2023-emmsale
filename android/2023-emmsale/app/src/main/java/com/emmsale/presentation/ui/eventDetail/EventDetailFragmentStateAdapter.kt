package com.emmsale.presentation.ui.eventDetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emmsale.presentation.ui.commentList.CommentFragment
import com.emmsale.presentation.ui.eventDetailInfo.EventInfoFragment
import com.emmsale.presentation.ui.recruitmentList.EventRecruitmentFragment

class EventDetailFragmentStateAdapter(
    fragmentActivity: FragmentActivity,
    private val eventId: Long,
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = EVENT_DETAIL_TAB_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            INFORMATION_TAB -> EventInfoFragment.create()
            COMMENT_TAB -> CommentFragment.create(eventId)
            RECRUITMENT_TAB -> EventRecruitmentFragment.create(eventId)
            else -> throw IllegalArgumentException(VIEWPAGER_ERROR_MESSAGE)
        }
    }

    companion object {
        private const val VIEWPAGER_ERROR_MESSAGE = "알수없는 ViewPager 오류입니다."
        private const val INFORMATION_TAB = 0
        private const val COMMENT_TAB = 1
        private const val RECRUITMENT_TAB = 2
        private const val EVENT_DETAIL_TAB_COUNT = 3
    }
}