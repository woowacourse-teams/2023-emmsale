import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.emmsale.R
import com.emmsale.databinding.FragmentEventParticipantBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.eventdetail.participant.uistate.ParticipantsUiState
import com.emmsale.presentation.eventdetail.participant.uistate.ParticipationStatusUiState
import com.emmsale.presentation.ui.eventdetail.participant.EventParticipantAdapter
import com.emmsale.presentation.ui.eventdetail.participant.EventParticipantViewModel

class EventParticipantFragment : BaseFragment<FragmentEventParticipantBinding>() {
    override val layoutResId: Int = R.layout.fragment_event_participant

    private val viewModel: EventParticipantViewModel by viewModels { EventParticipantViewModel.factory }
    private val eventId: Long by lazy {
        arguments?.getLong(EVENT_ID_KEY) ?: throw IllegalArgumentException("아이디못가져옴")
    }
    private val participantAdapter: EventParticipantAdapter by lazy {
        EventParticipantAdapter(::requestCompanion, ::showMemberProfile)
    }
    private val participationButton: AppCompatButton by lazy { binding.btnEventparticipantParticipate }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setUpParticipants()
        setUpRequestCompanion()
        setUpParticipation()
        participationButtonClick()
        setUpParticipationStatus()
        viewModel.fetchParticipants(eventId)
    }

    private fun initRecyclerView() {
        binding.rvParticipants.apply {
            adapter = participantAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun requestCompanion(memberId: Long) {
        viewModel.requestCompanion(eventId, memberId)
    }

    private fun showMemberProfile(memberId: Long) {
    }

    private fun setUpParticipants() {
        viewModel.participants.observe(viewLifecycleOwner) {
            when (it) {
                is ParticipantsUiState.Success -> {
                    participantAdapter.submitList(it.value)
                    viewModel.checkParticipationStatus(eventId)
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

    private fun setUpParticipation() {
        viewModel.participationSaving.observe(viewLifecycleOwner) { success ->
            if (success) {
                showToastMessage("참가 성공")
                viewModel.checkParticipationStatus(eventId)
            } else {
                showToastMessage("참가 실패")
            }
        }
    }

    private fun setUpParticipationStatus() {
        viewModel.isParticipate.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ParticipationStatusUiState.Success -> {
                    if (state.isParticipate) {
                        showToastMessage("참가 상태")
                        participationButton.isSelected = true
                        participationButton.text = "참가 취소"
                    } else {
                        showToastMessage("불참 상태")
                        participationButton.isSelected = false
                        participationButton.text = "참가 신청"
                    }
                }

                else -> showToastMessage("참여 여부 확인 불가")
            }
        }
    }

    private fun participationButtonClick() {
        participationButton.setOnClickListener {
            when (val state = viewModel.isParticipate.value) {
                is ParticipationStatusUiState.Success -> {
                    if (state.isParticipate) {
                        viewModel.deleteParticipant(eventId)
                    } else {
                        viewModel.saveParticipant(eventId)
                    }
                }

                else -> showToastMessage("서버 오류로 실행할 수 없어요")
            }
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"

        fun create(eventId: Long): EventParticipantFragment {
            val fragment = EventParticipantFragment()
            fragment.arguments = Bundle().apply {
                putLong(EVENT_ID_KEY, eventId)
            }
            return fragment
        }
    }
}
