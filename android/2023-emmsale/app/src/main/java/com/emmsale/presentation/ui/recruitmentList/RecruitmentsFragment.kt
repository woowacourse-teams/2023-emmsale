package com.emmsale.presentation.ui.recruitmentList

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentRecruitmentsBinding
import com.emmsale.model.Recruitment
import com.emmsale.presentation.base.NetworkFragment
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.common.recyclerView.DividerItemDecoration
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.recruitmentDetail.RecruitmentDetailActivity
import com.emmsale.presentation.ui.recruitmentList.RecruitmentsViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.recruitmentList.recyclerView.EventRecruitmentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecruitmentsFragment :
    NetworkFragment<FragmentRecruitmentsBinding>(R.layout.fragment_recruitments),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("event_recruitment") {

    override val viewModel: RecruitmentsViewModel by viewModels()

    private val recruitmentAdapter: EventRecruitmentAdapter by lazy {
        EventRecruitmentAdapter(::navigateToRecruitmentDetail)
    }

    private lateinit var eventDetailActivity: EventDetailActivity

    private fun navigateToRecruitmentDetail(recruitment: Recruitment) {
        RecruitmentDetailActivity.startActivity(
            context = requireContext(),
            eventId = viewModel.eventId,
            recruitmentId = recruitment.id,
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventDetailActivity = context as EventDetailActivity
        registerScreen(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        setupRecruitmentsRecyclerView()

        observeRecruitments()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setupRecruitmentsRecyclerView() {
        binding.rvRecruitment.adapter = recruitmentAdapter
        binding.rvRecruitment.addItemDecoration(DividerItemDecoration(requireContext()))
    }

    private fun observeRecruitments() {
        viewModel.recruitments.observe(viewLifecycleOwner) {
            recruitmentAdapter.submitList(it)
        }
    }

    companion object {
        fun create(eventId: Long): RecruitmentsFragment = RecruitmentsFragment().apply {
            arguments = Bundle().apply {
                putLong(EVENT_ID_KEY, eventId)
            }
        }
    }
}
