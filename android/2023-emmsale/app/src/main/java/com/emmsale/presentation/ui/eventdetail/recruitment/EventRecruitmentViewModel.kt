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
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentsUiState
import kotlinx.coroutines.launch

class EventRecruitmentViewModel(
    private val eventId: Long,
    private val recruitmentRepository: RecruitmentRepository,
) : ViewModel() {

    private val _recruitments: NotNullMutableLiveData<RecruitmentsUiState> = NotNullMutableLiveData(
        RecruitmentsUiState(),
    )
    val recruitments: NotNullLiveData<RecruitmentsUiState> = _recruitments

    private val _hasWritingPermission: MutableLiveData<Boolean> = MutableLiveData()
    val hasWritingPermission: LiveData<Boolean> = _hasWritingPermission

    init {
        fetchRecruitments()
        fetchHasWritingPermission()
    }

    fun fetchRecruitments() {
        setLoadingState(true)
        viewModelScope.launch {
            when (val response = recruitmentRepository.fetchEventRecruitments(eventId)) {
                is ApiSuccess -> fetchSuccessRecruitments(response.data)
                is ApiError -> changeToErrorState()
                is ApiException -> changeToErrorState()
            }
        }
    }

    private fun setLoadingState(state: Boolean) {
        _recruitments.postValue(_recruitments.value.copy(isLoading = state))
    }

    private fun fetchSuccessRecruitments(recruitments: List<Recruitment>) {
        _recruitments.postValue(RecruitmentsUiState.from(recruitments))
    }

    private fun changeToErrorState() {
        val value = _recruitments.value.copy(isError = true, isLoading = false)
        _recruitments.postValue(value)
    }

    private fun fetchHasWritingPermission() {
        viewModelScope.launch {
            when (val response = recruitmentRepository.checkHasWritingPermission(eventId)) {
                is ApiSuccess -> setHasPermissionWritingState(response.data)
                is ApiError -> setHasPermissionWritingState(false)
                is ApiException -> setHasPermissionWritingState(false)
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
