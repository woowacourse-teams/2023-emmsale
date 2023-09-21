package com.emmsale.presentation.ui.recruitmentList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.model.Recruitment
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.recruitmentList.uiState.RecruitmentPostsUiState
import kotlinx.coroutines.launch

class EventRecruitmentViewModel(
    private val eventId: Long,
    private val recruitmentRepository: RecruitmentRepository,
) : ViewModel(), Refreshable {

    private val _recruitments: NotNullMutableLiveData<RecruitmentPostsUiState> =
        NotNullMutableLiveData(
            RecruitmentPostsUiState(),
        )
    val recruitments: NotNullLiveData<RecruitmentPostsUiState> = _recruitments

    private val _hasWritingPermission: MutableLiveData<Boolean> = MutableLiveData()
    val hasWritingPermission: LiveData<Boolean> = _hasWritingPermission

    init {
        refresh()
    }

    override fun refresh() {
        fetchRecruitments()
        fetchHasWritingPermission()
    }

    fun fetchRecruitments() {
        changeRecruitmentsToLoadingState()
        viewModelScope.launch {
            when (val result = recruitmentRepository.getEventRecruitments(eventId)) {
                is Failure, NetworkError -> changeRecruitmentsToErrorState()
                is Success -> fetchSuccessRecruitments(result.data)
                is Unexpected -> throw Throwable(result.error)
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
            when (val result = recruitmentRepository.checkIsAlreadyPostRecruitment(eventId)) {
                is Failure, NetworkError -> setHasPermissionWritingState(false)
                is Success -> setHasPermissionWritingState(!result.data)
                is Unexpected -> throw Throwable(result.error)
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