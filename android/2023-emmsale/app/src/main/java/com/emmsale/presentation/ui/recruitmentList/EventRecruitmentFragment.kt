package com.emmsale.presentation.ui.recruitmentList

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.emmsale.R
import com.emmsale.databinding.FragmentEventRecruitmentBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.recruitmentDetail.RecruitmentPostDetailActivity
import com.emmsale.presentation.ui.recruitmentList.EventRecruitmentViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.recruitmentList.recyclerView.EventRecruitmentAdapter
import com.emmsale.presentation.ui.recruitmentList.uiState.RecruitmentPostUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventRecruitmentFragment :
    BaseFragment<FragmentEventRecruitmentBinding>(),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("event_recruitment") {
    override val layoutResId: Int = R.layout.fragment_event_recruitment
    private val viewModel: EventRecruitmentViewModel by viewModels()

    private val recruitmentAdapter: EventRecruitmentAdapter by lazy {
        EventRecruitmentAdapter(::navigateToRecruitmentDetail)
    }

    private lateinit var eventDetailActivity: EventDetailActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventDetailActivity = context as EventDetailActivity
        registerScreen(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        initRecyclerView()
        setUpRecruitments()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun navigateToRecruitmentDetail(recruitmentPostUiState: RecruitmentPostUiState) {
        val intent = RecruitmentPostDetailActivity.getIntent(
            requireContext(),
            viewModel.eventId,
            recruitmentPostUiState.id,
        )
        startActivity(intent)
    }

    private fun initRecyclerView() {
        binding.rvRecruitment.adapter = recruitmentAdapter
        binding.rvRecruitment.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUpRecruitments() {
        viewModel.recruitments.observe(viewLifecycleOwner) { recruitmentsUiState ->
            recruitmentAdapter.submitList(recruitmentsUiState.list)
        }
    }

    companion object {
        fun create(eventId: Long): EventRecruitmentFragment = EventRecruitmentFragment().apply {
            arguments = Bundle().apply {
                putLong(EVENT_ID_KEY, eventId)
            }
        }
    }
}
