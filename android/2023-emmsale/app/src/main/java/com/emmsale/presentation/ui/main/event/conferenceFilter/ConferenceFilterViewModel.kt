package com.emmsale.presentation.ui.main.event.conferenceFilter

import androidx.lifecycle.LiveData
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
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterDateOptionUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilteringOptionUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class ConferenceFilterViewModel(
    private val eventTagRepository: EventTagRepository,
    private val selectedStartDate: ConferenceFilterDateOptionUiState? = null,
    private val selectedEndDate: ConferenceFilterDateOptionUiState? = null,
) : ViewModel() {
    private val _conferenceFilter = NotNullMutableLiveData(ConferenceFilterUiState())
    val conferenceFilter: NotNullLiveData<ConferenceFilterUiState> = _conferenceFilter

    val isTagAllSelected: LiveData<Boolean> = _conferenceFilter.map { filter ->
        val tagFilterSize = filter.conferenceTagFilteringOptions.size
        val selectedTagFilterSize = filter.conferenceTagFilteringOptions.count { it.isSelected }
        tagFilterSize == selectedTagFilterSize
    }
    val isStartDateSelected: LiveData<Boolean> = _conferenceFilter.map { filters ->
        when {
            filters.selectedStartDate != null -> true
            else -> false
        }
    }

    private fun fetchEventFilters() {
        viewModelScope.launch {
            _conferenceFilter.value = _conferenceFilter.value.copy(isLoading = true)

            val statuses = fetchConferenceStatuses()
            val tags = fetchConferenceTags()

            _conferenceFilter.postValue(
                ConferenceFilterUiState(
                    conferenceStatusFilteringOptions = statuses,
                    conferenceTagFilteringOptions = tags,
                    selectedStartDate = selectedStartDate,
                    selectedEndDate = selectedEndDate,
                    isLoading = false,
                    isError = false,
                ),
            )
        }
    }

    // TODO: 추후 진행 상태에 대해 API를 통해 받아오도록 수정
    private fun fetchConferenceStatuses(): List<ConferenceFilteringOptionUiState> =
        listOf(
            Pair(1000L, "진행 중"),
            Pair(1001L, "진행 예정"),
            Pair(1002L, "마감"),
        ).map { (id, conferenceName) ->
            ConferenceFilteringOptionUiState(id, conferenceName)
        }

    private suspend fun fetchConferenceTags(): List<ConferenceFilteringOptionUiState> =
        withContext(Dispatchers.IO) {
            when (val eventTagResult = eventTagRepository.getEventTags(EventCategory.CONFERENCE)) {
                is ApiSuccess -> eventTagResult.data.map { eventTag ->
                    ConferenceFilteringOptionUiState(eventTag.id, eventTag.name)
                }

                is ApiError,
                is ApiException,
                -> {
                    _conferenceFilter.postValue(_conferenceFilter.value.copy(isError = true))
                    emptyList()
                }
            }
        }

    fun toggleFilterSelection(filter: ConferenceFilteringOptionUiState) {
        _conferenceFilter.value = _conferenceFilter.value.toggleFilterOptionSelection(filter.id)
    }

    fun updateFilters(filters: ConferenceFilterUiState?) {
        filters?.let(_conferenceFilter::postValue) ?: fetchEventFilters()
    }

    fun updateStartDate(startDate: LocalDate) {
        val filterDate =
            ConferenceFilterDateOptionUiState(startDate.year, startDate.monthValue, startDate.dayOfMonth)
        val isAfterThanEndDate = _conferenceFilter.value.selectedEndDate?.run {
            val endDate = LocalDate.of(year, month, day)
            startDate.isAfter(endDate)
        } == true

        if (isAfterThanEndDate) {
            _conferenceFilter.postValue(
                _conferenceFilter.value.copy(selectedStartDate = filterDate, selectedEndDate = null),
            )
            return
        }

        _conferenceFilter.postValue(
            _conferenceFilter.value.copy(selectedStartDate = filterDate),
        )
    }

    fun updateEndDate(endDate: LocalDate) {
        val selectedStartDate = _conferenceFilter.value.selectedStartDate
        val isEqualsOrBeforeThanStartDate = selectedStartDate?.run {
            val startDate = LocalDate.of(year, month, day)
            endDate.isEqual(startDate) || endDate.isBefore(startDate)
        } == true
        if (isEqualsOrBeforeThanStartDate) return

        val filterDate =
            ConferenceFilterDateOptionUiState(endDate.year, endDate.monthValue, endDate.dayOfMonth)
        _conferenceFilter.postValue(
            _conferenceFilter.value.copy(selectedEndDate = filterDate),
        )
    }

    fun clearFilters() {
        _conferenceFilter.postValue(_conferenceFilter.value.resetSelection())
    }

    companion object {
        val factory = ViewModelFactory {
            ConferenceFilterViewModel(
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository,
            )
        }
    }
}
