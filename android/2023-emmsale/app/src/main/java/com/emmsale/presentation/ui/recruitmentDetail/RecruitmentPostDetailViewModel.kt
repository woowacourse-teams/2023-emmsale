package com.emmsale.presentation.ui.recruitmentDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.model.Recruitment
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.firebase.analytics.logRecruitment
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.recruitmentDetail.uiState.HasOpenUrlUiState
import com.emmsale.presentation.ui.recruitmentDetail.uiState.RecruitmentPostDetailUiEvent
import com.emmsale.presentation.ui.recruitmentList.uiState.CompanionRequestTaskUiState
import com.emmsale.presentation.ui.recruitmentList.uiState.RecruitmentPostUiState
import kotlinx.coroutines.launch

class RecruitmentPostDetailViewModel(
    private val eventId: Long,
    private val recruitmentId: Long,
    private val recruitmentRepository: RecruitmentRepository,
    private val memberRepository: MemberRepository,
    tokenRepository: TokenRepository,
) : ViewModel(), Refreshable {

    private val _recruitmentPost: NotNullMutableLiveData<RecruitmentPostUiState> =
        NotNullMutableLiveData(RecruitmentPostUiState())
    val recruitmentPost: NotNullLiveData<RecruitmentPostUiState> = _recruitmentPost

    private val _companionRequest: NotNullMutableLiveData<CompanionRequestTaskUiState> =
        NotNullMutableLiveData(CompanionRequestTaskUiState())
    val companionRequest: NotNullLiveData<CompanionRequestTaskUiState> = _companionRequest

    private val _isDeletePostSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isDeletePostSuccess: LiveData<Boolean> = _isDeletePostSuccess

    private val _isAlreadyRequest: MutableLiveData<Boolean> = MutableLiveData(false)
    val isAlreadyRequest: LiveData<Boolean> = _isAlreadyRequest

    private val _event = MutableLiveData<RecruitmentPostDetailUiEvent?>(null)
    val event: LiveData<RecruitmentPostDetailUiEvent?> = _event

    private val _hasOpenProfileUrl: MutableLiveData<HasOpenUrlUiState> = MutableLiveData()
    val hasOpenProfileUrl: LiveData<HasOpenUrlUiState> = _hasOpenProfileUrl

    private val myUid = tokenRepository.getMyUid() ?: throw IllegalStateException(NOT_LOGIN_ERROR)

    init {
        refresh()
    }

    override fun refresh() {
        changeRecruitmentPostToLoadingState()
        viewModelScope.launch {
            when (val result = recruitmentRepository.getEventRecruitment(eventId, recruitmentId)) {
                is Failure, NetworkError -> changeRecruitmentPostToErrorState()
                is Success -> {
                    changeRecruitmentPostToSuccessState(result.data)
                    updateRecruitmentPostIsMyPostState()
                    checkIsAlreadyRequestCompanion()
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun requestCompanion(message: String) {
        changeRequestCompanionToLoadingState()
        viewModelScope.launch {
            val result = recruitmentRepository.requestCompanion(
                eventId = eventId,
                memberId = recruitmentPost.value.memberId,
                message = message,
            )
            when (result) {
                is Failure, NetworkError -> changeRequestCompanionToErrorState()
                is Success -> {
                    changeRequestCompanionToSuccessState()
                    setRequestCompanionIsAlreadyState(true)
                    logRecruitment(message, myUid)
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun deleteRecruitmentPost() {
        viewModelScope.launch {
            when (val result = recruitmentRepository.deleteRecruitment(eventId, recruitmentId)) {
                is Failure, NetworkError -> _isDeletePostSuccess.postValue(false)
                is Success -> _isDeletePostSuccess.postValue(true)
                is Unexpected -> throw Throwable(result.error)
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
                        _event.value = RecruitmentPostDetailUiEvent.REPORT_DUPLICATE
                    } else {
                        _event.value = RecruitmentPostDetailUiEvent.REPORT_ERROR
                    }
                }

                NetworkError ->
                    _event.value = RecruitmentPostDetailUiEvent.REPORT_ERROR

                is Success -> _event.value = RecruitmentPostDetailUiEvent.REPORT_SUCCESS
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun fetchProfile() {
        viewModelScope.launch {
            when (val result = memberRepository.getMember(myUid)) {
                is Failure, NetworkError -> _hasOpenProfileUrl.value = HasOpenUrlUiState.ERROR
                is Success -> {
                    if (result.data.openProfileUrl != "") {
                        _hasOpenProfileUrl.value =
                            HasOpenUrlUiState.TRUE
                    } else {
                        _hasOpenProfileUrl.value =
                            HasOpenUrlUiState.FALSE
                    }
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun removeEvent() {
        _event.value = null
    }

    private fun checkIsAlreadyRequestCompanion() {
        viewModelScope.launch {
            val result = recruitmentRepository.checkIsAlreadyRequestCompanion(
                eventId = eventId,
                senderId = myUid,
                receiverId = recruitmentPost.value.memberId,
            )
            when (result) {
                is Failure, NetworkError -> {}
                is Success -> setRequestCompanionIsAlreadyState(result.data)
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    private fun updateRecruitmentPostIsMyPostState() {
        val isMyPost = (recruitmentPost.value.memberId == myUid)
        _recruitmentPost.value = (_recruitmentPost.value.copy(isMyPost = isMyPost))
    }

    private fun changeRecruitmentPostToLoadingState() {
        _recruitmentPost.value = (_recruitmentPost.value.changeToLoadingState())
    }

    private fun changeRecruitmentPostToSuccessState(recruitment: Recruitment) {
        _recruitmentPost.value = (RecruitmentPostUiState.from(recruitment))
    }

    private fun changeRecruitmentPostToErrorState() {
        _recruitmentPost.value = _recruitmentPost.value.changeToErrorState()
    }

    private fun changeRequestCompanionToLoadingState() {
        _companionRequest.value = _companionRequest.value.changeToLoadingState()
    }

    private fun changeRequestCompanionToSuccessState() {
        _companionRequest.value = _companionRequest.value.changeToSuccessState()
    }

    private fun changeRequestCompanionToErrorState() {
        _companionRequest.value = _companionRequest.value.changeToErrorState()
    }

    private fun setRequestCompanionIsAlreadyState(state: Boolean) {
        _isAlreadyRequest.value = state
    }

    companion object {
        private const val NOT_LOGIN_ERROR = "로그인되지 않은 사용자는 이용할 수 없어요!!"
        private const val REPORT_DUPLICATE_ERROR_CODE = 400

        fun factory(
            eventId: Long,
            recruitmentId: Long,
        ) = ViewModelFactory {
            RecruitmentPostDetailViewModel(
                eventId = eventId,
                recruitmentId = recruitmentId,
                recruitmentRepository = KerdyApplication.repositoryContainer.recruitmentRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
            )
        }
    }
}
