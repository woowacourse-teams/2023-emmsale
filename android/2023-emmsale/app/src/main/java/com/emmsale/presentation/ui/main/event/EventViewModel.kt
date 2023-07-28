package com.emmsale.presentation.ui.main.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.event.EventRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.main.event.uistate.EventUiState
import com.emmsale.presentation.ui.main.event.uistate.EventsUiSTate
import kotlinx.coroutines.launch
import java.time.LocalDate

class EventViewModel(
    private val eventRepository: EventRepository,
    private val year: Int = LocalDate.now().year, // TODO(지금은 기기 시간대를 사용하고 있지만, 서버로부터 시간대를 받아와야 함)
    private val month: Int = LocalDate.now().monthValue, // TODO(위와 동일)
) : ViewModel() {
    private val _events = MutableLiveData<EventsUiSTate>()
    val events: LiveData<EventsUiSTate> = _events

    fun fetchEvents() {
        viewModelScope.launch {
            _events.value = EventsUiSTate.Loading
            when (val eventsResult = eventRepository.getEvents(year, month)) {
                is ApiSuccess -> _events.value =
                    EventsUiSTate.Success(eventsResult.data.map(EventUiState::from))

                is ApiError -> _events.value = EventsUiSTate.Error
                is ApiException -> _events.value = EventsUiSTate.Error
            }
        }
    }

    companion object {
        val factory = ViewModelFactory {
            EventViewModel(eventRepository = KerdyApplication.repositoryContainer.eventRepository)
        }
    }
}
