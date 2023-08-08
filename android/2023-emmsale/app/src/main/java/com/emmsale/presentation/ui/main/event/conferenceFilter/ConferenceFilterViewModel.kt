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
import java.time.LocalDate

class ConferenceFilterViewModel(
    private val eventTagRepository: EventTagRepository,
    private val selectedStartDate: ConferenceFilterDateUiState? = null,
    private val selectedEndDate: ConferenceFilterDateUiState? = null,
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
    val isStartDateSelected: LiveData<Boolean> = _eventFilters.map { filters ->
        when {
            filters is ConferenceFiltersUiState.Success && filters.selectedStartDate != null -> true
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
                ),
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

    fun updateStartDate(startDate: LocalDate) {
        val filterDate =
            ConferenceFilterDateUiState(startDate.year, startDate.monthValue, startDate.dayOfMonth)

        if ((_eventFilters.value as? ConferenceFiltersUiState.Success)?.selectedEndDate?.let {
                val endDate = LocalDate.of(it.year, it.month, it.day)
                startDate.isAfter(endDate)
            } == true
        ) {
            _eventFilters.postValue(
                (_eventFilters.value as? ConferenceFiltersUiState.Success)?.copy(
                    selectedStartDate = filterDate,
                    selectedEndDate = null,
                ),
            )
            return
        }

        _eventFilters.postValue(
            (_eventFilters.value as? ConferenceFiltersUiState.Success)?.copy(
                selectedStartDate = filterDate,
            ),
        )
    }

    fun updateEndDate(endDate: LocalDate) {
        if ((_eventFilters.value as? ConferenceFiltersUiState.Success)?.selectedStartDate?.let {
                val startDate = LocalDate.of(it.year, it.month, it.day)
                endDate.isBefore(startDate) || (endDate.year == startDate.year && endDate.monthValue == startDate.monthValue && endDate.dayOfMonth == startDate.dayOfMonth)
            } == true
        ) {
            return
        }

        val filterDate =
            ConferenceFilterDateUiState(endDate.year, endDate.monthValue, endDate.dayOfMonth)
        _eventFilters.postValue(
            (_eventFilters.value as? ConferenceFiltersUiState.Success)?.copy(
                selectedEndDate = filterDate,
            ),
        )
    }

    fun clearFilters() {
        val conferenceFilters = _eventFilters.value as? ConferenceFiltersUiState.Success ?: return
        _eventFilters.postValue(conferenceFilters.resetSelection())
        _selectedTagFilterCount.postValue(0)
    }

    companion object {
        val factory = ViewModelFactory {
            ConferenceFilterViewModel(
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository,
            )
        }
    }
}
