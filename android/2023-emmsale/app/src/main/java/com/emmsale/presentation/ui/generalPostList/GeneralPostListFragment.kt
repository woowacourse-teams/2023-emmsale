package com.emmsale.presentation.ui.generalPostList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentPostListBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.ui.feedDetail.FeedDetailActivity
import com.emmsale.presentation.ui.generalPostList.GeneralPostListViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.generalPostList.recyclerView.GeneralPostListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneralPostListFragment : BaseFragment<FragmentPostListBinding>() {
    override val layoutResId: Int = R.layout.fragment_post_list
    private val viewModel: GeneralPostListViewModel by viewModels()

    private val generalPostListAdapter: GeneralPostListAdapter by lazy {
        GeneralPostListAdapter(navigateToPostDetail = ::navigateToPostDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.rvPostlist.adapter = generalPostListAdapter
        setUpPosts()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setUpPosts() {
        viewModel.posts.observe(viewLifecycleOwner) {
            generalPostListAdapter.submitList(it.generalPosts)
        }
    }

    private fun navigateToPostDetail(feedId: Long) {
        FeedDetailActivity.startActivity(requireContext(), feedId)
    }

    companion object {
        fun create(eventId: Long): GeneralPostListFragment = GeneralPostListFragment().apply {
            arguments = Bundle().apply {
                putLong(EVENT_ID_KEY, eventId)
            }
        }
    }
}
