package com.emmsale.presentation.ui.recruitmentList

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.emmsale.R
import com.emmsale.databinding.FragmentEventRecruitmentBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.recruitmentDetail.RecruitmentPostDetailActivity
import com.emmsale.presentation.ui.recruitmentList.recyclerView.EventRecruitmentAdapter
import com.emmsale.presentation.ui.recruitmentList.uiState.RecruitmentPostUiState
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentPostWritingActivity

class EventRecruitmentFragment :
    BaseFragment<FragmentEventRecruitmentBinding>(),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("event_recruitment") {
    override val layoutResId: Int = R.layout.fragment_event_recruitment

    private val viewModel: EventRecruitmentViewModel by viewModels {
        EventRecruitmentViewModel.factory(eventId)
    }
    private val eventId: Long by lazy {
        arguments?.getLong(EVENT_ID_KEY) ?: throw IllegalArgumentException(EVENT_ID_NULL_ERROR)
    }
    private val recruitmentAdapter: EventRecruitmentAdapter by lazy {
        EventRecruitmentAdapter(::navigateToRecruitmentDetail)
    }

    private val postingResultActivityLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result == null || result.resultCode != AppCompatActivity.RESULT_OK) return@registerForActivityResult
            viewModel.fetchRecruitments()
            viewModel.fetchHasWritingPermission()
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
        initWritingButtonClickListener()
    }

    private fun navigateToRecruitmentDetail(recruitmentPostUiState: RecruitmentPostUiState) {
        val intent = RecruitmentPostDetailActivity.getIntent(
            requireContext(),
            eventId,
            recruitmentPostUiState.id,
        )
        postingResultActivityLauncher.launch(intent)
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

    private fun initWritingButtonClickListener() {
        binding.btnRecruitmentWriting.setOnClickListener {
            val hasPermission = viewModel.hasWritingPermission.value ?: return@setOnClickListener
            if (hasPermission) {
                val intent =
                    RecruitmentPostWritingActivity.getPostModeIntent(requireContext(), eventId)
                postingResultActivityLauncher.launch(intent)
            } else {
                binding.root.showSnackBar(getString(R.string.eventrecruitment_has_not_permission_writing))
            }
        }
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val EVENT_ID_NULL_ERROR = "행사 아이디를 가져오지 못했어요"

        fun create(eventId: Long): EventRecruitmentFragment {
            val fragment = EventRecruitmentFragment()
            fragment.arguments = Bundle().apply {
                putLong(EVENT_ID_KEY, eventId)
            }
            return fragment
        }
    }
}
