package com.emmsale.presentation.ui.main.event.conference

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.conference.ConferenceRepository
import com.emmsale.data.conference.EventCategory
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferencesUiState
import com.emmsale.presentation.ui.main.event.conference.uistate.EventsUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFiltersUiState
import kotlinx.coroutines.launch

class ConferenceViewModel(
    private val conferenceRepository: ConferenceRepository,
) : ViewModel() {
    private val _events = MutableLiveData<EventsUiState>()
    val events: LiveData<EventsUiState> = _events

    private val _selectedFilters = MutableLiveData<ConferenceFiltersUiState.Success>()
    val selectedFilters: LiveData<ConferenceFiltersUiState.Success> = _selectedFilters

    fun fetchEvents() {
        viewModelScope.launch {
            _events.value = EventsUiState.Loading
            when (val eventsResult = conferenceRepository.getConferences(
                category = EventCategory.CONFERENCE
            )) {
                is ApiSuccess -> _events.value =
                    EventsUiState.Success(eventsResult.data.map(ConferencesUiState::from))

                is ApiError -> _events.value = EventsUiState.Error
                is ApiException -> _events.value = EventsUiState.Error
            }
        }
    }

    fun updateConferenceFilter(conferenceFilter: ConferenceFiltersUiState.Success) {
        _selectedFilters.postValue(conferenceFilter)
    }

    companion object {
        val factory = ViewModelFactory {
            ConferenceViewModel(conferenceRepository = KerdyApplication.repositoryContainer.conferenceRepository)
        }
    }
}
