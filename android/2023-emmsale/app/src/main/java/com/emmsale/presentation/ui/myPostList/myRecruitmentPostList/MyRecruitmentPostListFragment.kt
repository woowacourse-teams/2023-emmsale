package com.emmsale.presentation.ui.myPostList.myRecruitmentPostList

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentMyRecruitmentPostListBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.ui.myPostList.myRecruitmentPostList.recyclerView.MyRecruitmentPostAdapter
import com.emmsale.presentation.ui.recruitmentDetail.RecruitmentPostDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyRecruitmentPostListFragment : BaseFragment<FragmentMyRecruitmentPostListBinding>() {

    override val layoutResId: Int = R.layout.fragment_my_recruitment_post_list

    private val viewModel: MyRecruitmentPostListViewModel by viewModels()

    private val adapter: MyRecruitmentPostAdapter by lazy {
        MyRecruitmentPostAdapter(::navigateToDetail)
    }

    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result == null || result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            viewModel.refresh()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMyRecruitmentPostList.adapter = adapter
        binding.vm = viewModel
        setUpMyRecruitmentPost()
    }

    private fun setUpMyRecruitmentPost() {
        viewModel.myPosts.observe(viewLifecycleOwner) {
            when (it.fetchResult) {
                FetchResult.SUCCESS -> adapter.submitList(it.successData)
                FetchResult.ERROR -> Unit
                FetchResult.LOADING -> Unit
            }
        }
    }

    private fun navigateToDetail(eventId: Long, recruitmentId: Long) {
        val intent = RecruitmentPostDetailActivity.getIntent(
            requireContext(),
            eventId = eventId,
            recruitmentId = recruitmentId,
            isNavigatedFromMyPost = true,
        )
        activityLauncher.launch(intent)
    }

    companion object {
        fun create(): MyRecruitmentPostListFragment {
            return MyRecruitmentPostListFragment()
        }
    }
}
