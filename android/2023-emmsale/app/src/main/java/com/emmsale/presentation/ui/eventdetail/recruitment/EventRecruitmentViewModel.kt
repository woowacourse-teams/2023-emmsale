package com.emmsale.presentation.ui.eventdetail.recruitment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.recruitment.RecruitmentRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.eventdetail.recruitment.uistate.RecruitmentStatusUiState
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentsUiState
import kotlinx.coroutines.launch

class EventRecruitmentViewModel(
    private val recruitmentRepository: RecruitmentRepository,
) : ViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _recruitments: MutableLiveData<RecruitmentsUiState> = MutableLiveData()
    val recruitments: LiveData<RecruitmentsUiState>
        get() = _recruitments

    private val _requestCompanion: MutableLiveData<Boolean> = MutableLiveData()
    val requestCompanion: LiveData<Boolean>
        get() = _requestCompanion

    private val _isRecruited: MutableLiveData<RecruitmentStatusUiState> = MutableLiveData()
    val isRecruited: LiveData<RecruitmentStatusUiState>
        get() = _isRecruited

    fun fetchRecruitments(eventId: Long) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            when (val response = recruitmentRepository.fetchEventRecruitments(eventId)) {
                is ApiSuccess -> {
                    _recruitments.postValue(RecruitmentsUiState.from(response.data))
                    _isLoading.postValue(false)
                }

                else -> _recruitments.postValue(RecruitmentsUiState.Error)
            }
        }
    }

    fun saveRecruitment(eventId: Long) {
        viewModelScope.launch {
            when (val response = recruitmentRepository.postRecruitment(eventId,"")) {
                is ApiSuccess -> _isRecruited.postValue(RecruitmentStatusUiState.Success(true))
                else -> _isRecruited.postValue(RecruitmentStatusUiState.Error)
            }
        }
    }

    fun deleteRecruitment(eventId: Long) {
        viewModelScope.launch {
            when (recruitmentRepository.deleteRecruitment(eventId,1L)) {
                is ApiSuccess -> {
                    _isRecruited.postValue(RecruitmentStatusUiState.Success(false))
                    fetchRecruitments(eventId)
                }

                else -> _isRecruited.postValue(RecruitmentStatusUiState.Error)
            }
        }
    }

    fun requestCompanion(eventId: Long, memberId: Long, message: String) {
        viewModelScope.launch {
            when (recruitmentRepository.requestCompanion(eventId, memberId, message)) {
                is ApiSuccess -> _requestCompanion.postValue(true)
                else -> _requestCompanion.postValue(false)
            }
        }
    }

    fun checkParticipationStatus(eventId: Long) {
        viewModelScope.launch {
            when (val response = recruitmentRepository.checkParticipationStatus(eventId)) {
                is ApiSuccess -> _isRecruited.postValue(
                    RecruitmentStatusUiState.Success(
                        response.data,
                    ),
                )

                else -> RecruitmentStatusUiState.Error
            }
        }
    }

    companion object {
        val factory = ViewModelFactory {
            EventRecruitmentViewModel(
                KerdyApplication.repositoryContainer.recruitmentRepository,
            )
        }
    }
}
