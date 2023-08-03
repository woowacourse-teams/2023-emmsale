package com.emmsale.presentation.ui.main.event.conferenceFilter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
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

    private val _selectedTagFilterCount = MutableLiveData<Int>()
    val isTagAllSelected: LiveData<Boolean> = _selectedTagFilterCount.map { count ->
        val filters = _eventFilters.value
        when {
            filters is ConferenceFiltersUiState.Success && count == filters.tags.size -> true
            else -> false
        }
    }

    private fun fetchEventFilters() {
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
            _selectedTagFilterCount.postValue(tags.count { it.isSelected })
        }
    }

    private fun fetchConferenceStatuses(): List<ConferenceFilterUiState> =
        listOf("진행 중", "진행 예정", "마감").mapIndexed { index, conferenceName ->
            ConferenceFilterUiState(index.toLong(), conferenceName)
        }

    private suspend fun fetchConferenceTags(): List<ConferenceFilterUiState> =
        withContext(Dispatchers.IO) {
            when (val eventTagResult = eventTagRepository.getEventTags(EventCategory.CONFERENCE)) {
                is ApiSuccess -> eventTagResult.data.map { eventTag ->
                    ConferenceFilterUiState(eventTag.id, eventTag.name)
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

    fun addSelectedTagFilterCount(count: Int) {
        _selectedTagFilterCount.value = (_selectedTagFilterCount.value ?: 0) + count
    }

    fun minusSelectedTagFilterCount(count: Int) {
        _selectedTagFilterCount.value = (_selectedTagFilterCount.value ?: 0) - count
    }

    fun updateFilters(filters: ConferenceFiltersUiState?) {
        filters?.let(_eventFilters::postValue) ?: fetchEventFilters()

        if (filters is ConferenceFiltersUiState.Success) {
            _selectedTagFilterCount.postValue(filters.tags.count { it.isSelected })
        }
    }

    companion object {
        val factory = ViewModelFactory {
            ConferenceFilterViewModel(
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository
            )
        }
    }
}
