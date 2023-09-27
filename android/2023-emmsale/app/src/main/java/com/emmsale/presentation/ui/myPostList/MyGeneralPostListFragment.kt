package com.emmsale.presentation.ui.myPostList

import androidx.fragment.app.Fragment
import com.emmsale.R
import com.emmsale.databinding.FragmentMyGeneralPostListBinding
import com.emmsale.presentation.base.BaseFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyGeneralPostListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyGeneralPostListFragment : BaseFragment<FragmentMyGeneralPostListBinding>() {

    override val layoutResId: Int = R.layout.fragment_my_general_post_list

    companion object {
        fun create(): MyGeneralPostListFragment {
            return MyGeneralPostListFragment()
        }
    }
}
