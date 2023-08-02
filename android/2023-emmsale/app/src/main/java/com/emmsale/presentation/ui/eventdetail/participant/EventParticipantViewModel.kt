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

    private val _participants: MutableLiveData<ParticipantsUiState> = MutableLiveData()
    val participants: LiveData<ParticipantsUiState>
        get() = _participants

    private val _requestCompanion: MutableLiveData<Boolean> = MutableLiveData()
    val requestCompanion: LiveData<Boolean>
        get() = _requestCompanion

    private val _participationSaving: MutableLiveData<Boolean> = MutableLiveData()
    val participationSaving: LiveData<Boolean>
        get() = _participationDeletion

    private val _participationDeletion: MutableLiveData<Boolean> = MutableLiveData()
    val participationDeletion: LiveData<Boolean>
        get() = _participationDeletion

    private val _isParticipate: MutableLiveData<ParticipationStatusUiState> = MutableLiveData()
    val isParticipate: LiveData<ParticipationStatusUiState>
        get() = _isParticipate

    fun fetchParticipants(eventId: Long) {
        viewModelScope.launch {
            when (val response = participantRepository.fetchEventParticipants(eventId)) {
                is ApiSuccess -> _participants.postValue(ParticipantsUiState.from(response.data))
                else -> _participants.postValue(ParticipantsUiState.Error)
            }
        }
    }

    fun saveParticipant(eventId: Long) {
        viewModelScope.launch {
            when (val response = participantRepository.saveParticipant(eventId)) {
                is ApiSuccess -> _participationDeletion.postValue(true)
                else -> _participationDeletion.postValue(false)
            }
        }
    }

    fun deleteParticipant(eventId: Long) {
        viewModelScope.launch {
            when (participantRepository.deleteParticipant(eventId)) {
                is ApiSuccess -> _participationDeletion.postValue(true)
                else -> _participationDeletion.postValue(false)
            }
        }
    }

    fun requestCompanion(eventId: Long, memberId: Long) {
        viewModelScope.launch {
            when (participantRepository.requestCompanion(eventId, memberId, "")) {
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

    private fun getRequestMessage(): String {
        return ""
    }

    companion object {
        val factory = ViewModelFactory {
            EventParticipantViewModel(
                KerdyApplication.repositoryContainer.participantRepository,
            )
        }
    }
}
