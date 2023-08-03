package com.emmsale.presentation.ui.eventdetail.participant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.participant.ParticipantRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.eventdetail.participant.uistate.ParticipantsUiState
import com.emmsale.presentation.eventdetail.participant.uistate.ParticipationStatusUiState
import kotlinx.coroutines.launch

class EventParticipantViewModel(
    private val participantRepository: ParticipantRepository,
) : ViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _participants: MutableLiveData<ParticipantsUiState> = MutableLiveData()
    val participants: LiveData<ParticipantsUiState>
        get() = _participants

    private val _requestCompanion: MutableLiveData<Boolean> = MutableLiveData()
    val requestCompanion: LiveData<Boolean>
        get() = _requestCompanion

    private val _isParticipate: MutableLiveData<ParticipationStatusUiState> = MutableLiveData()
    val isParticipate: LiveData<ParticipationStatusUiState>
        get() = _isParticipate

    fun fetchParticipants(eventId: Long) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            when (val response = participantRepository.fetchEventParticipants(eventId)) {
                is ApiSuccess -> {
                    _participants.postValue(ParticipantsUiState.from(response.data))
                    _isLoading.postValue(false)
                }

                else -> _participants.postValue(ParticipantsUiState.Error)
            }
        }
    }

    fun saveParticipant(eventId: Long) {
        viewModelScope.launch {
            when (val response = participantRepository.saveParticipant(eventId)) {
                is ApiSuccess -> _isParticipate.postValue(ParticipationStatusUiState.Success(true))
                else -> _isParticipate.postValue(ParticipationStatusUiState.Error)
            }
        }
    }

    fun deleteParticipant(eventId: Long) {
        viewModelScope.launch {
            when (participantRepository.deleteParticipant(eventId)) {
                is ApiSuccess -> {
                    _isParticipate.postValue(ParticipationStatusUiState.Success(false))
                    fetchParticipants(eventId)
                }

                else -> _isParticipate.postValue(ParticipationStatusUiState.Error)
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
                    ParticipationStatusUiState.Success(
                        response.data,
                    ),
                )

                else -> ParticipationStatusUiState.Error
            }
        }
    }

    companion object {
        val factory = ViewModelFactory {
            EventParticipantViewModel(
                KerdyApplication.repositoryContainer.participantRepository,
            )
        }
    }
}
