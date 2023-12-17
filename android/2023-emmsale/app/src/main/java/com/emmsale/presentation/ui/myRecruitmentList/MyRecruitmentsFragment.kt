package com.emmsale.presentation.ui.myRecruitmentList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentMyRecruitmentsBinding
import com.emmsale.presentation.base.NetworkFragment
import com.emmsale.presentation.common.recyclerView.DividerItemDecoration
import com.emmsale.presentation.ui.myRecruitmentList.recyclerView.MyRecruitmentAdapter
import com.emmsale.presentation.ui.recruitmentDetail.RecruitmentDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyRecruitmentsFragment :
    NetworkFragment<FragmentMyRecruitmentsBinding>(R.layout.fragment_my_recruitments) {

    override val viewModel: MyRecruitmentViewModel by viewModels()

    private val myRecruitmentAdapter: MyRecruitmentAdapter by lazy {
        MyRecruitmentAdapter(navigateToDetail = ::navigateToDetail)
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
        viewModel.myRecruitments.observe(viewLifecycleOwner) { myRecruitments ->
            myRecruitmentAdapter.submitList(myRecruitments)
        }
    }

    private fun setupRecyclerView() {
        binding.rvMyPost.apply {
            adapter = myRecruitmentAdapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(context = context))
        }
    }

    private fun navigateToDetail(eventId: Long, recruitmentId: Long) {
        RecruitmentDetailActivity.startActivity(
            requireContext(),
            eventId = eventId,
            recruitmentId = recruitmentId,
        )
    }
}
