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
    private val tokenRepository: TokenRepository,
) : ViewModel() {
    private val _recruitmentPost: NotNullMutableLiveData<RecruitmentPostUiState> =
        NotNullMutableLiveData(RecruitmentPostUiState())
    val recruitmentPost: NotNullLiveData<RecruitmentPostUiState> = _recruitmentPost

    private val _companionRequest: NotNullMutableLiveData<CompanionRequestUiState> =
        NotNullMutableLiveData(CompanionRequestUiState())
    val companionRequest: NotNullLiveData<CompanionRequestUiState> = _companionRequest

    private val _isPostDeleteSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isPostDeleteSuccess: LiveData<Boolean> = _isPostDeleteSuccess
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
                is ApiSuccess -> changeRequestCompanionToSuccessState()
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

    private fun updateRecruitmentPostIsMyPostState() {
        val isMyPost = (recruitmentPost.value.memberId == tokenRepository.getMyUid())
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

    companion object {
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
