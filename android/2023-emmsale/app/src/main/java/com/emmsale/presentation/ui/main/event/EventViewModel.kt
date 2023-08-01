package com.emmsale.presentation.ui.main.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.event.EventCategory
import com.emmsale.data.event.EventRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.main.event.uistate.EventUiState
import com.emmsale.presentation.ui.main.event.uistate.EventsUiState
import kotlinx.coroutines.launch

class EventViewModel(
    private val eventRepository: EventRepository,
) : ViewModel() {
    private val _events = MutableLiveData<EventsUiState>()
    val events: LiveData<EventsUiState> = _events

    fun fetchEvents() {
        viewModelScope.launch {
            _events.value = EventsUiState.Loading
            when (val eventsResult = eventRepository.getConferences(
                category = EventCategory.CONFERENCE
            )) {
                is ApiSuccess -> _events.value =
                    EventsUiState.Success(eventsResult.data.map(EventUiState::from))

                is ApiError -> _events.value = EventsUiState.Error
                is ApiException -> _events.value = EventsUiState.Error
            }
        }
    }

    companion object {
        val factory = ViewModelFactory {
            EventViewModel(eventRepository = KerdyApplication.repositoryContainer.eventRepository)
        }
    }
}
