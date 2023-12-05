package com.emmsale.presentation.ui.recruitmentDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.emmsale.data.repository.interfaces.MessageRoomRepository
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.recruitmentDetail.uiState.RecruitmentPostDetailUiEvent
import com.emmsale.presentation.ui.recruitmentDetail.uiState.RecruitmentUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class RecruitmentPostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recruitmentRepository: RecruitmentRepository,
    private val messageRoomRepository: MessageRoomRepository,
    tokenRepository: TokenRepository,
) : RefreshableViewModel() {
    val eventId: Long = requireNotNull(savedStateHandle[EVENT_ID_KEY]) {
        "[ERROR] 행사 아이디를 가져오지 못했어요."
    }
    val recruitmentId: Long = requireNotNull(savedStateHandle[RECRUITMENT_ID_KEY]) {
        "[ERROR] 모집글 아이디를 가져오지 못했어요."
    }

    private val _recruitment: NotNullMutableLiveData<RecruitmentUiState> =
        NotNullMutableLiveData(RecruitmentUiState())
    val recruitment: NotNullLiveData<RecruitmentUiState> = _recruitment

    private val _canSendMessage = NotNullMutableLiveData(true)
    val canSendMessage: NotNullLiveData<Boolean> = _canSendMessage

    private val _uiEvent = SingleLiveEvent<RecruitmentPostDetailUiEvent>()
    val uiEvent: LiveData<RecruitmentPostDetailUiEvent> = _uiEvent

    private val myUid = requireNotNull(tokenRepository.getMyUid()) {
        "[ERROR] 내 아이디를 가져오지 못했어요."
    }

    init {
        fetchRecruitment()
    }

    private fun fetchRecruitment(): Job = fetchData(
        fetchData = { recruitmentRepository.getEventRecruitment(eventId, recruitmentId) },
        onSuccess = { _recruitment.value = RecruitmentUiState(it, myUid) },
        onFailure = { _, _ -> _uiEvent.value = RecruitmentPostDetailUiEvent.PostFetchFail },
    )

    override fun refresh(): Job = refreshData(
        refresh = { recruitmentRepository.getEventRecruitment(eventId, recruitmentId) },
        onSuccess = { _recruitment.value = RecruitmentUiState(it, myUid) },
    )

    fun deleteRecruitment(): Job = command(
        command = { recruitmentRepository.deleteRecruitment(eventId, recruitmentId) },
        onSuccess = { _uiEvent.value = RecruitmentPostDetailUiEvent.PostDeleteComplete },
        onFailure = { _, _ ->
            _uiEvent.value = RecruitmentPostDetailUiEvent.PostDeleteFail
        },
    )

    fun reportRecruitment(): Job = command(
        command = {
            recruitmentRepository.reportRecruitment(
                recruitmentId = _recruitment.value.recruitment.id,
                authorId = _recruitment.value.recruitment.writer.id,
                reporterId = myUid,
            )
        },
        onSuccess = { _uiEvent.value = RecruitmentPostDetailUiEvent.ReportComplete },
        onFailure = { code, _ ->
            if (code == REPORT_DUPLICATE_ERROR_CODE) {
                _uiEvent.value = RecruitmentPostDetailUiEvent.ReportDuplicate
            } else {
                _uiEvent.value = RecruitmentPostDetailUiEvent.ReportFail
            }
        },
    )

    fun sendMessage(message: String): Job = command(
        command = {
            messageRoomRepository.sendMessage(
                senderId = myUid,
                receiverId = _recruitment.value.recruitment.writer.id,
                message = message,
            )
        },
        onSuccess = {
            _uiEvent.value = RecruitmentPostDetailUiEvent.MessageSendComplete(
                roomId = it,
                otherId = _recruitment.value.recruitment.writer.id,
            )
        },
        onFailure = { _, _ ->
            _uiEvent.value = RecruitmentPostDetailUiEvent.MessageSendFail
        },
        onStart = { _canSendMessage.value = false },
        onFinish = { _canSendMessage.value = true },
    )

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
        const val RECRUITMENT_ID_KEY = "RECRUITMENT_ID_KEY"

        private const val REPORT_DUPLICATE_ERROR_CODE = 400
    }
}
