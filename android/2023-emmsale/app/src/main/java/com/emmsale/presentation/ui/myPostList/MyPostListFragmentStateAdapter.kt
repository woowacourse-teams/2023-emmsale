package com.emmsale.presentation.ui.myPostList

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emmsale.presentation.ui.myPostList.myGeneralPostList.MyGeneralPostListFragment
import com.emmsale.presentation.ui.myPostList.myRecruitmentPostList.MyRecruitmentPostListFragment

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

enum class PostType(val position: Int) {
    GENERAL(0), RECRUITMENT(1);

    companion object {
        fun from(position: Int): PostType? = PostType.values().find { it.position == position }
    }
}
