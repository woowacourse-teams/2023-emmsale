package com.emmsale.presentation.ui.recruitmentDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.messageRoom.MessageRoomRepository
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.recruitmentDetail.uiState.RecruitmentPostDetailUiEvent
import com.emmsale.presentation.ui.recruitmentList.uiState.RecruitmentPostUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecruitmentPostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recruitmentRepository: RecruitmentRepository,
    private val messageRoomRepository: MessageRoomRepository,
    tokenRepository: TokenRepository,
) : ViewModel(), Refreshable {
    val eventId: Long = requireNotNull(savedStateHandle[EVENT_ID_KEY]) {
        "[ERROR] 행사 아이디를 가져오지 못했어요."
    }
    val recruitmentId: Long = requireNotNull(savedStateHandle[RECRUITMENT_ID_KEY]) {
        "[ERROR] 모집글 아이디를 가져오지 못했어요."
    }

    private val _recruitmentPost: NotNullMutableLiveData<RecruitmentPostUiState> =
        NotNullMutableLiveData(RecruitmentPostUiState.Loading)
    val recruitmentPost: NotNullLiveData<RecruitmentPostUiState> = _recruitmentPost

    private val _isAlreadyRequest: MutableLiveData<Boolean> = MutableLiveData(false)
    val isAlreadyRequest: LiveData<Boolean> = _isAlreadyRequest

    private val _event = MutableLiveData<RecruitmentPostDetailUiEvent?>(null)
    val event: LiveData<RecruitmentPostDetailUiEvent?> = _event

    private val _uiEvent: NotNullMutableLiveData<Event<RecruitmentPostDetailUiEvent>> =
        NotNullMutableLiveData(Event(RecruitmentPostDetailUiEvent.None))
    val uiEvent: NotNullLiveData<Event<RecruitmentPostDetailUiEvent>> = _uiEvent

    private val myUid = requireNotNull(tokenRepository.getMyUid()) {
        "[ERROR] 내 아이디를 가져오지 못했어요."
    }

    init {
        refresh()
    }

    override fun refresh() {
        viewModelScope.launch {
            when (val result = recruitmentRepository.getEventRecruitment(eventId, recruitmentId)) {
                is Failure -> _uiEvent.value = Event(RecruitmentPostDetailUiEvent.PostFetchFail)
                NetworkError -> _recruitmentPost.value = _recruitmentPost.value.copy(isError = true)
                is Success -> {
                    _recruitmentPost.value = RecruitmentPostUiState.create(result.data, myUid)
                }

                is Unexpected ->
                    _uiEvent.value =
                        Event(RecruitmentPostDetailUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    fun deleteRecruitmentPost() {
        viewModelScope.launch {
            when (val result = recruitmentRepository.deleteRecruitment(eventId, recruitmentId)) {
                is Failure -> _uiEvent.value = Event(RecruitmentPostDetailUiEvent.PostDeleteFail)
                NetworkError -> _recruitmentPost.value = _recruitmentPost.value.copy(isError = true)
                is Success ->
                    _uiEvent.value =
                        Event(RecruitmentPostDetailUiEvent.PostDeleteComplete)

                is Unexpected ->
                    _uiEvent.value =
                        Event(RecruitmentPostDetailUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    fun reportRecruitment() {
        viewModelScope.launch {
            val result = recruitmentRepository.reportRecruitment(
                recruitmentId,
                recruitmentPost.value.memberId,
                myUid,
            )
            when (result) {
                is Failure -> {
                    if (result.code == REPORT_DUPLICATE_ERROR_CODE) {
                        _uiEvent.value = Event(RecruitmentPostDetailUiEvent.ReportDuplicate)
                    } else {
                        _uiEvent.value = Event(RecruitmentPostDetailUiEvent.ReportFail)
                    }
                }

                NetworkError ->
                    _recruitmentPost.value = _recruitmentPost.value.copy(isError = true)

                is Success -> _uiEvent.value = Event(RecruitmentPostDetailUiEvent.ReportComplete)
                is Unexpected ->
                    _uiEvent.value =
                        Event(RecruitmentPostDetailUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            when (
                val result =
                    messageRoomRepository.sendMessage(
                        myUid,
                        _recruitmentPost.value.memberId,
                        message,
                    )
            ) {
                is Failure ->
                    _uiEvent.value = Event(RecruitmentPostDetailUiEvent.MessageSendFail)

                NetworkError -> _recruitmentPost.value = _recruitmentPost.value.copy(isError = true)
                is Success ->
                    _uiEvent.value = Event(
                        RecruitmentPostDetailUiEvent.MessageSendComplete(
                            result.data,
                            _recruitmentPost.value.memberId,
                        ),
                    )

                is Unexpected ->
                    _uiEvent.value =
                        Event(RecruitmentPostDetailUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
        const val RECRUITMENT_ID_KEY = "RECRUITMENT_ID_KEY"

        private const val REPORT_DUPLICATE_ERROR_CODE = 400
    }
}
