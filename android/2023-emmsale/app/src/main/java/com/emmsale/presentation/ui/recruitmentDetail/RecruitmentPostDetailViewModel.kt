package com.emmsale.presentation.ui.recruitmentDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
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
            val response = recruitmentRepository.getEventRecruitment(eventId, recruitmentId)
            when (response) {
                is ApiSuccess -> {
                    changeRecruitmentPostToSuccessState(response.data)
                    updateRecruitmentPostIsMyPostState()
                    checkIsAlreadyRequestCompanion()
                }

                is ApiError, is ApiException -> changeRecruitmentPostToErrorState()
            }
        }
    }

    fun requestCompanion(message: String) {
        changeRequestCompanionToLoadingState()
        viewModelScope.launch {
            val response = recruitmentRepository.requestCompanion(
                eventId = eventId,
                memberId = recruitmentPost.value.memberId,
                message = message,
            )
            when (response) {
                is ApiSuccess -> {
                    changeRequestCompanionToSuccessState()
                    setRequestCompanionIsAlreadyState(true)
                    logRecruitment(message, myUid)
                }

                is ApiError, is ApiException -> changeRequestCompanionToErrorState()
            }
        }
    }

    fun deleteRecruitmentPost() {
        viewModelScope.launch {
            when (recruitmentRepository.deleteRecruitment(eventId, recruitmentId)) {
                is ApiSuccess -> _isDeletePostSuccess.postValue(true)
                is ApiError, is ApiException -> _isDeletePostSuccess.postValue(false)
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
                is ApiError -> {
                    if (result.code == REPORT_DUPLICATE_ERROR_CODE) {
                        _event.value = RecruitmentPostDetailUiEvent.REPORT_DUPLICATE
                    } else {
                        _event.value = RecruitmentPostDetailUiEvent.REPORT_ERROR
                    }
                }

                is ApiException ->
                    _event.value = RecruitmentPostDetailUiEvent.REPORT_ERROR

                is ApiSuccess -> _event.value = RecruitmentPostDetailUiEvent.REPORT_SUCCESS
            }
        }
    }

    fun fetchProfile() {
        viewModelScope.launch {
            when (val response = memberRepository.getMember(myUid)) {
                is ApiSuccess -> {
                    if (response.data.openProfileUrl != "") {
                        _hasOpenProfileUrl.value =
                            HasOpenUrlUiState.TRUE
                    } else {
                        _hasOpenProfileUrl.value =
                            HasOpenUrlUiState.FALSE
                    }
                }

                else -> _hasOpenProfileUrl.value = HasOpenUrlUiState.ERROR
            }
        }
    }

    fun removeEvent() {
        _event.value = null
    }

    private fun checkIsAlreadyRequestCompanion() {
        viewModelScope.launch {
            val response = recruitmentRepository.checkIsAlreadyRequestCompanion(
                eventId = eventId,
                senderId = myUid,
                receiverId = recruitmentPost.value.memberId,
            )
            when (response) {
                is ApiSuccess -> setRequestCompanionIsAlreadyState(response.data)
                is ApiError, is ApiException -> {}
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
