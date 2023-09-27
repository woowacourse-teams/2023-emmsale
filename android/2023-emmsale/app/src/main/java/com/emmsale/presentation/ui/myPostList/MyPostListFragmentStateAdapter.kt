package com.emmsale.presentation.ui.myPostList

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emmsale.presentation.ui.myPostList.uiState.PostType

class MyPostListFragmentStateAdapter(activity: MyPostActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return PostType.values().size
    }

    override fun createFragment(position: Int): Fragment {
        return when (PostType.from(position)) {
            PostType.GENERAL -> MyGeneralPostListFragment.create()
            PostType.RECRUITMENT -> MyRecruitmentPostListFragment.create()
            null -> MyGeneralPostListFragment.create()
        }
    }
}
