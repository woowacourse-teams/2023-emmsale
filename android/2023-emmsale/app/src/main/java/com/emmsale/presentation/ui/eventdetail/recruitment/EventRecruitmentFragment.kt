import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.emmsale.R
import com.emmsale.databinding.FragmentEventRecruitmentBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.ui.eventdetail.recruitment.EventParticipantAdapter
import com.emmsale.presentation.ui.eventdetail.recruitment.EventRecruitmentViewModel
import com.emmsale.presentation.ui.eventdetail.recruitment.RecruitmentFragmentDialog
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentStatusUiState
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentsUiState

class EventRecruitmentFragment : BaseFragment<FragmentEventRecruitmentBinding>() {
    override val layoutResId: Int = R.layout.fragment_event_recruitment

    private val viewModel: EventRecruitmentViewModel by viewModels { EventRecruitmentViewModel.factory }
    private val eventId: Long by lazy {
        arguments?.getLong(EVENT_ID_KEY) ?: throw IllegalArgumentException("아이디못가져옴")
    }
    private val participantAdapter: EventParticipantAdapter by lazy {
        EventParticipantAdapter(::showRequestDialog, ::showMemberProfile)
    }
    private val participationButton: AppCompatButton by lazy { binding.btnEventparticipantParticipate }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setUpParticipants()
        setUpRequestCompanion()
        setUpParticipationStatus()
        viewModel.fetchParticipants(eventId)
    }

    private fun initRecyclerView() {
        binding.rvParticipants.apply {
            adapter = participantAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showRequestDialog(memberId: Long, memberName: String) {
        RecruitmentFragmentDialog(memberName, memberId, ::requestCompanion).show(
            parentFragmentManager,
            "Participant",
        )
    }

    private fun requestCompanion(memberId: Long, message: String) {
        viewModel.requestCompanion(eventId, memberId, message)
    }

    private fun showMemberProfile(memberId: Long) {
    }

    private fun setUpParticipants() {
        viewModel.participants.observe(viewLifecycleOwner) {
            when (it) {
                is RecruitmentsUiState.Success -> {
                    participantAdapter.submitList(it.value)
                    viewModel.checkParticipationStatus(eventId)
                    participationButtonClick()
                }

                else -> showToastMessage("참가자들 불러오기 실패")
            }
        }
    }

    private fun setUpRequestCompanion() {
        viewModel.requestCompanion.observe(viewLifecycleOwner) { success ->
            if (success) {
                showToastMessage("요청 성공")
            } else {
                showToastMessage("요청 실패")
            }
        }
    }

    private fun setUpParticipationStatus() {
        viewModel.isParticipate.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RecruitmentStatusUiState.Success -> {
                    if (state.isParticipate) {
                        setButtonParticipationState()
                    } else {
                        setButtonAbsenceState()
                    }
                }

                else -> showToastMessage("참가 여부 확인이 불가합니다")
            }
        }
    }

    private fun setButtonParticipationState() {
        participationButton.isSelected = true
        participationButton.text = "참가 취소"
    }

    private fun setButtonAbsenceState() {
        participationButton.isSelected = false
        participationButton.text = "참가 신청"
    }

    private fun participationButtonClick() {
        participationButton.setOnClickListener {
            when (val state = viewModel.isParticipate.value) {
                is RecruitmentStatusUiState.Success -> {
                    if (state.isParticipate) {
                        viewModel.deleteParticipant(eventId)
                        showToastMessage("참가를 취소합니다")
                    } else {
                        viewModel.saveParticipant(eventId)
                        showToastMessage("참가합니다~")
                    }
                }

                else -> showToastMessage("서버 오류로 실행할 수 없어요")
            }
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
