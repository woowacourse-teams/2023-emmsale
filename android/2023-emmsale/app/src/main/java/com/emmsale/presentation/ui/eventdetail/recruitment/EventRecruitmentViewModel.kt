package com.emmsale.presentation.ui.eventdetail.recruitment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.participant.ParticipantRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentStatusUiState
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentsUiState
import kotlinx.coroutines.launch

class EventRecruitmentViewModel(
    private val participantRepository: ParticipantRepository,
) : ViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _participants: MutableLiveData<RecruitmentsUiState> = MutableLiveData()
    val participants: LiveData<RecruitmentsUiState>
        get() = _participants

    private val _requestCompanion: MutableLiveData<Boolean> = MutableLiveData()
    val requestCompanion: LiveData<Boolean>
        get() = _requestCompanion

    private val _isParticipate: MutableLiveData<RecruitmentStatusUiState> = MutableLiveData()
    val isParticipate: LiveData<RecruitmentStatusUiState>
        get() = _isParticipate

    fun fetchParticipants(eventId: Long) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            when (val response = participantRepository.fetchEventParticipants(eventId)) {
                is ApiSuccess -> {
                    _participants.postValue(RecruitmentsUiState.from(response.data))
                    _isLoading.postValue(false)
                }

                else -> _participants.postValue(RecruitmentsUiState.Error)
            }
        }
    }

    fun saveParticipant(eventId: Long) {
        viewModelScope.launch {
            when (val response = participantRepository.saveParticipant(eventId)) {
                is ApiSuccess -> _isParticipate.postValue(RecruitmentStatusUiState.Success(true))
                else -> _isParticipate.postValue(RecruitmentStatusUiState.Error)
            }
        }
    }

    fun deleteParticipant(eventId: Long) {
        viewModelScope.launch {
            when (participantRepository.deleteParticipant(eventId)) {
                is ApiSuccess -> {
                    _isParticipate.postValue(RecruitmentStatusUiState.Success(false))
                    fetchParticipants(eventId)
                }

                else -> _isParticipate.postValue(RecruitmentStatusUiState.Error)
            }
        }
    }

    fun requestCompanion(eventId: Long, memberId: Long, message: String) {
        viewModelScope.launch {
            when (participantRepository.requestCompanion(eventId, memberId, message)) {
                is ApiSuccess -> _requestCompanion.postValue(true)
                else -> _requestCompanion.postValue(false)
            }
        }
    }

    fun checkParticipationStatus(eventId: Long) {
        viewModelScope.launch {
            when (val response = participantRepository.checkParticipationStatus(eventId)) {
                is ApiSuccess -> _isParticipate.postValue(
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
                KerdyApplication.repositoryContainer.participantRepository,
            )
        }
    }
}
