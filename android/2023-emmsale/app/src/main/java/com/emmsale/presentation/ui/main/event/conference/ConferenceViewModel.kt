package com.emmsale.presentation.ui.main.event.conference

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.conference.ConferenceRepository
import com.emmsale.data.conference.ConferenceStatus
import com.emmsale.data.conference.EventCategory
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferenceUiState
import com.emmsale.presentation.ui.main.event.conference.uistate.EventsUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterDateOptionUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilteringOptionUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterUiState
import kotlinx.coroutines.launch

class ConferenceViewModel(
    private val conferenceRepository: ConferenceRepository,
) : ViewModel() {
    private val _events = NotNullMutableLiveData(EventsUiState())
    val events: NotNullLiveData<EventsUiState> = _events

    private val _selectedFilters = MutableLiveData<ConferenceFilterUiState>()
    val selectedFilters: LiveData<ConferenceFilterUiState> = _selectedFilters

    init {
        fetchConferences()
    }

    private fun fetchConferences(
        startDate: String? = null,
        endDate: String? = null,
        statuses: List<ConferenceStatus> = emptyList(),
        tags: List<String> = emptyList(),
    ) {
        viewModelScope.launch {
            _events.value = _events.value.copy(isLoading = true)
            when (
                val eventsResult = conferenceRepository.getConferences(
                    category = EventCategory.CONFERENCE,
                    startDate = startDate,
                    endDate = endDate,
                    statuses = statuses,
                    tags = tags,
                )
            ) {
                is ApiSuccess ->
                    _events.value = _events.value.copy(
                        events = eventsResult.data.map(ConferenceUiState::from),
                        isLoading = false,
                    )

                is ApiError,
                is ApiException,
                -> _events.value = _events.value.copy(isError = true, isLoading = false)
            }
        }
    }

    fun updateConferenceFilter(conferenceFilter: ConferenceFilterUiState) {
        _selectedFilters.postValue(conferenceFilter)
        fetchConferences(
            startDate = conferenceFilter.selectedStartDate?.toDateString(),
            endDate = conferenceFilter.selectedEndDate?.toDateString(),
            statuses = conferenceFilter.conferenceStatusFilteringOptions
                .filter { it.isSelected }
                .map { it.toStatus() },
            tags = conferenceFilter.conferenceTagFilteringOptions
                .filter { it.isSelected }
                .map { it.name },
        )
    }

    private fun ConferenceFilterDateOptionUiState.toDateString(): String {
        val year = year.toString().padStart(2, '0')
        val month = month.toString().padStart(2, '0')
        val day = day.toString().padStart(2, '0')
        return "$year-$month-$day"
    }

    private fun ConferenceFilteringOptionUiState.toStatus(): ConferenceStatus = when (id) {
        1000L -> ConferenceStatus.IN_PROGRESS
        1001L -> ConferenceStatus.SCHEDULED
        1002L -> ConferenceStatus.ENDED
        else -> throw IllegalArgumentException("Unknown status id: $id")
    }

    companion object {
        val factory = ViewModelFactory {
            ConferenceViewModel(conferenceRepository = KerdyApplication.repositoryContainer.conferenceRepository)
        }
    }
}
