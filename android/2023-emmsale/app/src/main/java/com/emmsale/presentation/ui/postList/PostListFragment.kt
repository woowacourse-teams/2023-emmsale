package com.emmsale.presentation.ui.postList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentPostListBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.ui.feedDetail.FeedDetailActivity
import com.emmsale.presentation.ui.postList.PostListViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.postList.recyclerView.PostListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostListFragment : BaseFragment<FragmentPostListBinding>() {
    override val layoutResId: Int = R.layout.fragment_post_list
    private val viewModel: PostListViewModel by viewModels()

    private val postListAdapter: PostListAdapter by lazy {
        PostListAdapter(navigateToPostDetail = ::navigateToPostDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.rvPostlist.adapter = postListAdapter
        setUpPosts()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setUpPosts() {
        viewModel.posts.observe(viewLifecycleOwner) {
            postListAdapter.submitList(it.posts)
        }
    }

    private fun navigateToPostDetail(feedId: Long) {
        FeedDetailActivity.startActivity(requireContext(), feedId)
    }

    companion object {
        fun create(eventId: Long): PostListFragment = PostListFragment().apply {
            arguments = Bundle().apply {
                putLong(EVENT_ID_KEY, eventId)
            }
        }
    }
}
