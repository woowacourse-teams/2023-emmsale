package com.emmsale.presentation.ui.eventdetail.recruitment.writing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.recruitment.RecruitmentRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentPostWritingUiState
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.WritingModeUiState.EDIT
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.WritingModeUiState.POST
import kotlinx.coroutines.launch

class RecruitmentPostWritingViewModel(
    private val eventId: Long,
    private val recruitmentIdToEdit: Long?,
    private val recruitmentRepository: RecruitmentRepository,
) : ViewModel() {
    private val _recruitmentWriting: NotNullMutableLiveData<RecruitmentPostWritingUiState> =
        NotNullMutableLiveData(RecruitmentPostWritingUiState())
    val recruitmentWriting: NotNullLiveData<RecruitmentPostWritingUiState> =
        _recruitmentWriting

    private val _postedRecruitmentId: NotNullMutableLiveData<Long> =
        NotNullMutableLiveData(DEFAULT_POSTED_RECRUITMENT_ID)
    val postedRecruitmentId: NotNullLiveData<Long> = _postedRecruitmentId

    init {
        if (recruitmentIdToEdit != null) {
            changeToEditMode()
        } else {
           changeToPostMode()
        }
    }

    private fun changeToPostMode() {
        _recruitmentWriting.value = _recruitmentWriting.value.setWritingMode(
            POST,
        )
    }

    private fun changeToEditMode() {
        _recruitmentWriting.value = _recruitmentWriting.value.setWritingMode(
            EDIT,
        )
    }

    fun postRecruitment(content: String) {
        changeToLoadingState()
        viewModelScope.launch {
            when (val response = recruitmentRepository.postRecruitment(eventId, content)) {
                is ApiSuccess -> {
                    _postedRecruitmentId.postValue(response.data)
                    changeToPostSuccessState()
                }

                is ApiError, is ApiException -> changeToErrorState()
            }
        }
    }

    fun editRecruitment(content: String) {
        changeToLoadingState()
        if (recruitmentIdToEdit == null) {
            changeToErrorState()
            return
        }
        viewModelScope.launch {
            when (recruitmentRepository.editRecruitment(eventId, recruitmentIdToEdit, content)) {
                is ApiSuccess -> changeToEditSuccessState()
                is ApiError, is ApiException -> changeToErrorState()
            }
        }
    }

    private fun changeToLoadingState() {
        _recruitmentWriting.postValue(_recruitmentWriting.value.changeToLoadingState())
    }

    private fun changeToErrorState() {
        _recruitmentWriting.postValue(_recruitmentWriting.value.changeToErrorState())
    }

    private fun changeToPostSuccessState() {
        _recruitmentWriting.postValue(_recruitmentWriting.value.changeToPostSuccessState())
    }

    private fun changeToEditSuccessState() {
        _recruitmentWriting.postValue(_recruitmentWriting.value.changeToEditSuccessState())
    }

    companion object {
        private const val DEFAULT_POSTED_RECRUITMENT_ID = -1L
        fun factory(eventId: Long, recruitmentId: Long?) = ViewModelFactory {
            RecruitmentPostWritingViewModel(
                eventId,
                recruitmentId,
                KerdyApplication.repositoryContainer.recruitmentRepository,
            )
        }
    }
}
