package com.emmsale.presentation.ui.main.event.conferenceFilter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.conference.EventCategory
import com.emmsale.data.eventTag.EventTagRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterDateUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFiltersUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ConferenceFilterViewModel(
    private val eventTagRepository: EventTagRepository,
    private val selectedStartDate: ConferenceFilterDateUiState = ConferenceFilterDateUiState(
        LocalDateTime.now().year, LocalDateTime.now().month.value
    ),
    private val selectedEndDate: ConferenceFilterDateUiState = ConferenceFilterDateUiState(
        LocalDateTime.now().year, LocalDateTime.now().month.value
    ),
) : ViewModel() {
    private val _eventFilters = MutableLiveData<ConferenceFiltersUiState>()
    val eventFilters: LiveData<ConferenceFiltersUiState> = _eventFilters

    fun fetchEventFilters() {
        viewModelScope.launch {
            _eventFilters.postValue(ConferenceFiltersUiState.Loading)

            val statuses = fetchConferenceStatuses()
            val tags = fetchConferenceTags()

            _eventFilters.postValue(
                ConferenceFiltersUiState.Success(
                    statuses = statuses,
                    tags = tags,
                    selectedStartDate = selectedStartDate,
                    selectedEndDate = selectedEndDate,
                )
            )
        }
    }

    private fun fetchConferenceStatuses(): List<ConferenceFilterUiState> =
        listOf("진행 중", "진행 예정", "마감").map { conferenceName ->
            ConferenceFilterUiState(conferenceName)
        }

    private suspend fun fetchConferenceTags(): List<ConferenceFilterUiState> =
        withContext(Dispatchers.IO) {
            when (val eventTagResult = eventTagRepository.getEventTags(EventCategory.CONFERENCE)) {
                is ApiSuccess -> eventTagResult.data.map { eventTag ->
                    ConferenceFilterUiState(name = eventTag.name)
                }

                is ApiError,
                is ApiException,
                -> {
                    _eventFilters.postValue(ConferenceFiltersUiState.Error)
                    emptyList()
                }
            }
        }

    fun toggleFilterSelection(filter: ConferenceFilterUiState) {
        filter.isSelected = !filter.isSelected
    }

    fun updateFilters(filters: ConferenceFiltersUiState?) {
        filters?.let(_eventFilters::postValue) ?: fetchEventFilters()
    }

    companion object {
        val factory = ViewModelFactory {
            ConferenceFilterViewModel(
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository
            )
        }
    }
}
