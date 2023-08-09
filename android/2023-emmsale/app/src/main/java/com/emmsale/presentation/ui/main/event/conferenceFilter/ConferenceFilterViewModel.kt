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
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
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
    private val _eventFilters = NotNullMutableLiveData(ConferenceFiltersUiState())
    val eventFilters: NotNullLiveData<ConferenceFiltersUiState> = _eventFilters

    private val _selectedTagFilterCount = MutableLiveData<Int>()
    val isTagAllSelected: LiveData<Boolean> = _selectedTagFilterCount.map { count ->
        when (count) {
            _eventFilters.value.conferenceTagFilters.size -> true
            else -> false
        }
    }
    val isStartDateSelected: LiveData<Boolean> = _eventFilters.map { filters ->
        when {
            filters.selectedStartDate != null -> true
            else -> false
        }
    }

    private fun fetchEventFilters() {
        viewModelScope.launch {
            _eventFilters.value = _eventFilters.value.copy(isLoading = true)

            val statuses = fetchConferenceStatuses()
            val tags = fetchConferenceTags()

            _eventFilters.postValue(
                ConferenceFiltersUiState(
                    conferenceStatusFilters = statuses,
                    conferenceTagFilters = tags,
                    selectedStartDate = selectedStartDate,
                    selectedEndDate = selectedEndDate,
                    isLoading = false,
                    isError = false,
                ),
            )
            _selectedTagFilterCount.postValue(tags.count { it.isSelected })
        }
    }

    // TODO: 추후 진행 상태에 대해 API를 통해 받아오도록 수정
    private fun fetchConferenceStatuses(): List<ConferenceFilterUiState> =
        listOf(
            Pair(1000L, "진행 중"),
            Pair(1001L, "진행 예정"),
            Pair(1002L, "마감"),
        ).map { (id, conferenceName) ->
            ConferenceFilterUiState(id, conferenceName)
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
                    _eventFilters.postValue(_eventFilters.value.copy(isError = true))
                    emptyList()
                }
            }
        }

    fun toggleFilterSelection(filter: ConferenceFilterUiState) {
        _eventFilters.value = _eventFilters.value.toggleFilterSelection(filter.id)
    }

    fun addSelectedTagFilterCount(count: Int) {
        _selectedTagFilterCount.value = (_selectedTagFilterCount.value ?: 0) + count
    }

    fun minusSelectedTagFilterCount(count: Int) {
        _selectedTagFilterCount.value = (_selectedTagFilterCount.value ?: 0) - count
    }

    fun updateFilters(filters: ConferenceFiltersUiState?) {
        filters?.let(_eventFilters::postValue) ?: fetchEventFilters()
        _selectedTagFilterCount.postValue(filters?.conferenceTagFilters?.count { it.isSelected })
    }

    fun updateStartDate(startDate: LocalDate) {
        val filterDate =
            ConferenceFilterDateUiState(startDate.year, startDate.monthValue, startDate.dayOfMonth)
        val isAfterThanEndDate = _eventFilters.value.selectedEndDate?.run {
            val endDate = LocalDate.of(year, month, day)
            startDate.isAfter(endDate)
        } == true

        if (isAfterThanEndDate) {
            _eventFilters.postValue(
                _eventFilters.value.copy(selectedStartDate = filterDate, selectedEndDate = null),
            )
            return
        }

        _eventFilters.postValue(
            _eventFilters.value.copy(selectedStartDate = filterDate),
        )
    }

    fun updateEndDate(endDate: LocalDate) {
        val selectedStartDate = _eventFilters.value.selectedStartDate
        val isEqualsOrBeforeThanStartDate = selectedStartDate?.run {
            val startDate = LocalDate.of(year, month, day)
            endDate.isEqual(startDate) || endDate.isBefore(startDate)
        } == true
        if (isEqualsOrBeforeThanStartDate) return

        val filterDate =
            ConferenceFilterDateUiState(endDate.year, endDate.monthValue, endDate.dayOfMonth)
        _eventFilters.postValue(
            _eventFilters.value.copy(
                selectedEndDate = filterDate,
            ),
        )
    }

    fun clearFilters() {
        _eventFilters.postValue(_eventFilters.value.resetSelection())
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
