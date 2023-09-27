package com.emmsale.presentation.ui.myPostList

import android.os.Bundle
import android.view.View
import com.emmsale.R
import com.emmsale.databinding.FragmentMyRecruitmentPostListBinding
import com.emmsale.presentation.base.BaseFragment

class MyRecruitmentPostListFragment : BaseFragment<FragmentMyRecruitmentPostListBinding>() {

    override val layoutResId: Int = R.layout.fragment_my_recruitment_post_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun create(): MyRecruitmentPostListFragment {
            return MyRecruitmentPostListFragment()
        }
    }
}
