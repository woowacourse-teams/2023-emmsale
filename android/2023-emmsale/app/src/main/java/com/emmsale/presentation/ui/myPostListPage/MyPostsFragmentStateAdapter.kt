package com.emmsale.presentation.ui.myPostListPage

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emmsale.presentation.ui.myFeedList.MyFeedsFragment
import com.emmsale.presentation.ui.myRecruitmentList.MyRecruitmentsFragment

class MyPostsFragmentStateAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = TAB_SIZE

    override fun createFragment(position: Int): Fragment = when (position) {
        MY_FEEDS_POSITION -> MyFeedsFragment()
        MY_RECRUITMENT_POSITION -> MyRecruitmentsFragment()
        else -> throw IllegalArgumentException("올바르지 않은 fragment position 입니다.")
    }

    companion object {
        private const val TAB_SIZE = 2
        private const val MY_FEEDS_POSITION = 0
        private const val MY_RECRUITMENT_POSITION = 1
    }
}
