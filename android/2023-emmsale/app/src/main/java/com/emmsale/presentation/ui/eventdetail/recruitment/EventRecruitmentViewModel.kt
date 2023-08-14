package com.emmsale.presentation.ui.eventdetail.recruitment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.recruitment.Recruitment
import com.emmsale.data.recruitment.RecruitmentRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentPostsUiState
import kotlinx.coroutines.launch

class EventRecruitmentViewModel(
    private val eventId: Long,
    private val recruitmentRepository: RecruitmentRepository,
) : ViewModel() {

    private val _recruitments: NotNullMutableLiveData<RecruitmentPostsUiState> =
        NotNullMutableLiveData(
            RecruitmentPostsUiState(),
        )
    val recruitments: NotNullLiveData<RecruitmentPostsUiState> = _recruitments

    private val _hasWritingPermission: MutableLiveData<Boolean> = MutableLiveData()
    val hasWritingPermission: LiveData<Boolean> = _hasWritingPermission

    init {
        fetchRecruitments()
        fetchHasWritingPermission()
    }

    fun fetchRecruitments() {
        changeRecruitmentsToLoadingState()
        viewModelScope.launch {
            when (val response = recruitmentRepository.getEventRecruitments(eventId)) {
                is ApiSuccess -> fetchSuccessRecruitments(response.data)
                is ApiError, is ApiException -> changeRecruitmentsToErrorState()
            }
        }
    }

    private fun changeRecruitmentsToLoadingState() {
        _recruitments.postValue(_recruitments.value.changeToLoadingState())
    }

    private fun changeRecruitmentsToErrorState() {
        _recruitments.postValue(_recruitments.value.changeToErrorState())
    }

    private fun fetchSuccessRecruitments(recruitments: List<Recruitment>) {
        _recruitments.postValue(RecruitmentPostsUiState.from(recruitments))
    }

    fun fetchHasWritingPermission() {
        viewModelScope.launch {
            when (val response = recruitmentRepository.checkIsAlreadyPostRecruitment(eventId)) {
                is ApiSuccess -> setHasPermissionWritingState(!response.data)
                is ApiError, is ApiException -> setHasPermissionWritingState(false)
            }
        }
    }

    private fun setHasPermissionWritingState(state: Boolean) {
        _hasWritingPermission.postValue(state)
    }

    companion object {
        fun factory(eventId: Long) = ViewModelFactory {
            EventRecruitmentViewModel(
                eventId,
                KerdyApplication.repositoryContainer.recruitmentRepository,
            )
        }
    }
}
