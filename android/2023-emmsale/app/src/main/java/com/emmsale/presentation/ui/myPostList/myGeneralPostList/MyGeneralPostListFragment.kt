package com.emmsale.presentation.ui.myPostList.myGeneralPostList

import android.os.Bundle
import android.view.View
import com.emmsale.R
import com.emmsale.databinding.FragmentMyGeneralPostListBinding
import com.emmsale.presentation.base.BaseFragment

class MyGeneralPostListFragment : BaseFragment<FragmentMyGeneralPostListBinding>() {

    override val layoutResId: Int = R.layout.fragment_my_general_post_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    companion object {
        fun create(): MyGeneralPostListFragment {
            return MyGeneralPostListFragment()
        }
    }
}
