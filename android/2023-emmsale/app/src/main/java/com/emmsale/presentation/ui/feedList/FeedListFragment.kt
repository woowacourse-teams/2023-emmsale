package com.emmsale.presentation.ui.feedList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentFeedListBinding
import com.emmsale.presentation.base.NetworkFragment
import com.emmsale.presentation.ui.feedDetail.FeedDetailActivity
import com.emmsale.presentation.ui.feedList.FeedListViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.feedList.recyclerView.FeedListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedListFragment : NetworkFragment<FragmentFeedListBinding>() {
    override val layoutResId: Int = R.layout.fragment_feed_list
    override val viewModel: FeedListViewModel by viewModels()

    private val feedListAdapter: FeedListAdapter by lazy {
        FeedListAdapter(navigateToFeedDetail = ::navigateToFeedDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.rvFeedList.adapter = feedListAdapter
        setUpFeeds()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setUpFeeds() {
        viewModel.feeds.observe(viewLifecycleOwner) {
            feedListAdapter.submitList(it)
        }
    }

    private fun navigateToFeedDetail(feedId: Long) {
        FeedDetailActivity.startActivity(requireContext(), feedId)
    }

    companion object {
        fun create(eventId: Long): FeedListFragment = FeedListFragment().apply {
            arguments = Bundle().apply {
                putLong(EVENT_ID_KEY, eventId)
            }
        }
    }
}
