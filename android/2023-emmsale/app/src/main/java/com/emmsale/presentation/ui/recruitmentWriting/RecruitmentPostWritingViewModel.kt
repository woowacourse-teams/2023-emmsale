package com.emmsale.presentation.ui.recruitmentWriting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.presentation.common.firebase.analytics.logWriting
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.recruitmentList.uiState.RecruitmentPostWritingUiState
import com.emmsale.presentation.ui.recruitmentList.uiState.WritingModeUiState.EDIT
import com.emmsale.presentation.ui.recruitmentList.uiState.WritingModeUiState.POST
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecruitmentPostWritingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recruitmentRepository: RecruitmentRepository,
) : ViewModel() {
    val eventId: Long = savedStateHandle[EVENT_ID_KEY] ?: DEFAULT_EVENT_ID
    private val recruitmentIdToEdit: Long? = savedStateHandle[RECRUITMENT_ID_KEY]
    val recruitmentContentToEdit = savedStateHandle[RECRUITMENT_CONTENT_KEY] ?: DEFAULT_CONTENT

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
            when (val result = recruitmentRepository.postRecruitment(eventId, content)) {
                is Failure, NetworkError -> changeToErrorState()
                is Success -> {
                    _postedRecruitmentId.value = result.data
                    changeToPostSuccessState()
                    logWriting("recruitment", content, eventId)
                }

                is Unexpected -> throw Throwable(result.error)
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
            when (
                val result =
                    recruitmentRepository.editRecruitment(eventId, recruitmentIdToEdit, content)
            ) {
                is Failure, NetworkError -> changeToErrorState()
                is Success -> changeToEditSuccessState()
                is Unexpected -> throw Throwable(result.error)
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
        _recruitmentWriting.value = _recruitmentWriting.value.changeToPostSuccessState()
    }

    private fun changeToEditSuccessState() {
        _recruitmentWriting.postValue(_recruitmentWriting.value.changeToEditSuccessState())
    }

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_EVENT_ID = -1L

        const val RECRUITMENT_ID_KEY = "RECRUITMENT_ID_KEY"

        const val RECRUITMENT_CONTENT_KEY = "RECRUITMENT_CONTENT_KEY"
        private const val DEFAULT_CONTENT = ""

        private const val DEFAULT_POSTED_RECRUITMENT_ID = -1L
    }
}
