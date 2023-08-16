import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.FragmentEventRecruitmentBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.eventdetail.recruitment.EventRecruitmentViewModel
import com.emmsale.presentation.ui.eventdetail.recruitment.detail.RecruitmentPostDetailActivity
import com.emmsale.presentation.ui.eventdetail.recruitment.recyclerview.EventRecruitmentAdapter
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentPostUiState
import com.emmsale.presentation.ui.eventdetail.recruitment.writing.RecruitmentPostWritingActivity

class EventRecruitmentFragment : BaseFragment<FragmentEventRecruitmentBinding>() {
    override val layoutResId: Int = R.layout.fragment_event_recruitment

    private val viewModel: EventRecruitmentViewModel by viewModels {
        EventRecruitmentViewModel.factory(
            eventId,
        )
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        initRecyclerView()
        setUpRecruitments()
        initWritingButtonClickListener()
        initRecyclerViewListener()
    }

    override fun onResume() {
        super.onResume()
        eventDetailActivity.showEventInformation()
    }

    private fun initRecyclerViewListener() {
        binding.rvRecruitment.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                handleEventInformationVisibility(newState)
            }
        })
    }

    private fun handleEventInformationVisibility(newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE && isScrollTop()) {
            eventDetailActivity.showEventInformation()
        } else if (!isScrollTop()) {
            eventDetailActivity.hideEventInformation()
        }
    }

    private fun isScrollTop(): Boolean =
        !binding.rvRecruitment.canScrollVertically(TOP_CONDITION)

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
            if (recruitmentsUiState.isError) {
                requireContext().showToast(getString(R.string.eventrecruitment_fetch_recruitments_error_message))
            } else {
                recruitmentAdapter.submitList(recruitmentsUiState.list)
            }
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
                requireContext().showToast(getString(R.string.eventrecruitment_has_not_permission_writing))
            }
        }
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val EVENT_ID_NULL_ERROR = "행사 아이디를 가져오지 못했어요"
        private const val TOP_CONDITION = -1

        fun create(eventId: Long): EventRecruitmentFragment {
            val fragment = EventRecruitmentFragment()
            fragment.arguments = Bundle().apply {
                putLong(EVENT_ID_KEY, eventId)
            }
            return fragment
        }
    }
}
