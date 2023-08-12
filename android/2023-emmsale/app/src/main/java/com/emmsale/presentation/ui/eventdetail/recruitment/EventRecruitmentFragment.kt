import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.emmsale.R
import com.emmsale.databinding.FragmentEventRecruitmentBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.eventdetail.recruitment.EventRecruitmentViewModel
import com.emmsale.presentation.ui.eventdetail.recruitment.recyclerview.EventRecruitmentAdapter
import com.emmsale.presentation.ui.eventdetail.recruitment.writing.RecruitmentWritingActivity

class EventRecruitmentFragment : BaseFragment<FragmentEventRecruitmentBinding>() {
    override val layoutResId: Int = R.layout.fragment_event_recruitment

    private val viewModel: EventRecruitmentViewModel by viewModels {
        EventRecruitmentViewModel.factory(
            eventId,
        )
    }
    private val eventId: Long by lazy {
        arguments?.getLong(EVENT_ID_KEY) ?: throw IllegalArgumentException("아이디못가져옴")
    }
    private val recruitmentAdapter: EventRecruitmentAdapter by lazy {
        EventRecruitmentAdapter(::showMemberProfile)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initWritingButtonClickListener()
        setUpRecruitments()
    }

    private fun initRecyclerView() {
        binding.rvRecruitment.apply {
            adapter = recruitmentAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showMemberProfile(memberId: Long) = requireContext().showToast("맴버 보여주기")

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
                RecruitmentWritingActivity.startActivity(requireContext(), eventId)
            } else {
                requireContext().showToast(getString(R.string.recruitment_has_not_permission_writing))
            }
        }
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"

        fun create(eventId: Long): EventRecruitmentFragment {
            val fragment = EventRecruitmentFragment()
            fragment.arguments = Bundle().apply {
                putLong(EVENT_ID_KEY, eventId)
            }
            return fragment
        }
    }
}
