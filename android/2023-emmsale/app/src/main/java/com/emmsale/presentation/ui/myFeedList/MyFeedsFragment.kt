package com.emmsale.presentation.ui.myFeedList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentMyFeedsBinding
import com.emmsale.presentation.base.NetworkFragment
import com.emmsale.presentation.common.recyclerView.DividerItemDecoration
import com.emmsale.presentation.ui.feedDetail.FeedDetailActivity
import com.emmsale.presentation.ui.myFeedList.recyclerView.MyFeedAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFeedsFragment : NetworkFragment<FragmentMyFeedsBinding>(R.layout.fragment_my_feeds) {

    override val viewModel: MyFeedsViewModel by viewModels()

    private val myFeedAdapter: MyFeedAdapter by lazy {
        MyFeedAdapter(navigateToDetail = ::navigateToDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinding()
        setupRecyclerView()
        observeMyRecruitments()
    }

    private fun setupBinding() {
        binding.vm = viewModel
    }

    private fun observeMyRecruitments() {
        viewModel.myFeeds.observe(viewLifecycleOwner) { myFeeds ->
            myFeedAdapter.submitList(myFeeds)
        }
    }

    private fun setupRecyclerView() {
        binding.rvMyFeeds.apply {
            adapter = myFeedAdapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(context = context))
        }
    }

    private fun navigateToDetail(feedId: Long) {
        FeedDetailActivity.startActivity(
            requireContext(),
            feedId = feedId,
        )
    }
}
