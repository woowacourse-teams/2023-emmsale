package com.emmsale.presentation.ui.eventdetail.recruitment.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.recruitment.Recruitment
import com.emmsale.data.recruitment.RecruitmentRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.CompanionRequestUiState
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentPostUiState
import kotlinx.coroutines.launch

class RecruitmentPostDetailViewModel(
    private val eventId: Long,
    private val recruitmentId: Long,
    private val recruitmentRepository: RecruitmentRepository,
    tokenRepository: TokenRepository,
) : ViewModel() {
    private val _recruitmentPost: NotNullMutableLiveData<RecruitmentPostUiState> =
        NotNullMutableLiveData(RecruitmentPostUiState())
    val recruitmentPost: NotNullLiveData<RecruitmentPostUiState> = _recruitmentPost

    private val _companionRequest: NotNullMutableLiveData<CompanionRequestUiState> =
        NotNullMutableLiveData(CompanionRequestUiState())
    val companionRequest: NotNullLiveData<CompanionRequestUiState> = _companionRequest

    private val _isPostDeleteSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isPostDeleteSuccess: LiveData<Boolean> = _isPostDeleteSuccess

    private val myUid = tokenRepository.getMyUid() ?: throw IllegalStateException(NOT_LOGIN_ERROR)

    init {
        fetchRecruitmentPost()
    }

    fun fetchRecruitmentPost() {
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
                }

                is ApiError, is ApiException -> changeRequestCompanionToErrorState()
            }
        }
    }

    fun deleteRecruitmentPost() {
        viewModelScope.launch {
            when (recruitmentRepository.deleteRecruitment(eventId, recruitmentId)) {
                is ApiSuccess -> _isPostDeleteSuccess.postValue(true)
                is ApiError, is ApiException -> _isPostDeleteSuccess.postValue(false)
            }
        }
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
        _companionRequest.value = _companionRequest.value.setIsAlreadyRequestState(state)
    }

    companion object {
        private const val NOT_LOGIN_ERROR = "로그인되지 않은 사용자는 이용할 수 없어요!!"
        fun factory(
            eventId: Long,
            recruitmentId: Long,
        ) = ViewModelFactory {
            RecruitmentPostDetailViewModel(
                eventId,
                recruitmentId,
                KerdyApplication.repositoryContainer.recruitmentRepository,
                KerdyApplication.repositoryContainer.tokenRepository,
            )
        }
    }
}
