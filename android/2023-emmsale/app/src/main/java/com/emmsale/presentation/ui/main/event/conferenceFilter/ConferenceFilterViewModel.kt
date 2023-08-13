package com.emmsale.presentation.ui.main.event.conferenceFilter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.conference.EventCategory
import com.emmsale.data.conferenceStatus.ConferenceStatusRepository
import com.emmsale.data.eventTag.EventTagRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilteringDateOptionUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilteringOptionUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class ConferenceFilterViewModel(
    private val eventTagRepository: EventTagRepository,
    private val conferenceStatusRepository: ConferenceStatusRepository,
    private val selectedStartDate: ConferenceFilteringDateOptionUiState? = null,
    private val selectedEndDate: ConferenceFilteringDateOptionUiState? = null,
) : ViewModel() {
    private val _conferenceFilter = NotNullMutableLiveData(ConferenceFilterUiState())
    val conferenceFilter: NotNullLiveData<ConferenceFilterUiState> = _conferenceFilter

    val isTagAllSelected: LiveData<Boolean> = _conferenceFilter.map { filter ->
        val tagFilterSize = filter.conferenceTagFilteringOptions.size
        val selectedTagFilterSize = filter.conferenceTagFilteringOptions.count { it.isSelected }
        tagFilterSize == selectedTagFilterSize
    }
    val isStartDateSelected: LiveData<Boolean> = _conferenceFilter.map { filters ->
        filters.selectedStartDate != null
    }
    private var isAlreadyUpdatedFilterSelection = false

    private suspend fun fetchEventFilters() {
        _conferenceFilter.value = _conferenceFilter.value.copy(isLoading = true)

        val statuses = fetchConferenceStatuses()
        val tags = fetchConferenceTags()

        _conferenceFilter.value = ConferenceFilterUiState(
            conferenceStatusFilteringOptions = statuses,
            conferenceTagFilteringOptions = tags,
            selectedStartDate = selectedStartDate,
            selectedEndDate = selectedEndDate,
            isLoading = false,
            isLoadingConferenceFilterFailed = false,
        )
    }

    private suspend fun fetchConferenceTags(): List<ConferenceFilteringOptionUiState> =
        withContext(Dispatchers.IO) {
            when (val conferenceTag = eventTagRepository.getEventTags(EventCategory.CONFERENCE)) {
                is ApiSuccess -> conferenceTag.data.map(ConferenceFilteringOptionUiState::from)
                is ApiError, is ApiException -> {
                    _conferenceFilter.value = _conferenceFilter.value.copy(isLoadingConferenceFilterFailed = true)
                    emptyList()
                }
            }
        }

    private suspend fun fetchConferenceStatuses(): List<ConferenceFilteringOptionUiState> =
        conferenceStatusRepository.getConferenceStatuses()
            .map(ConferenceFilteringOptionUiState::from)

    fun toggleFilterSelection(filter: ConferenceFilteringOptionUiState) {
        _conferenceFilter.value = _conferenceFilter.value.toggleSelectionBy(filter.id)
    }

    fun updateFilteringOptionsToSelectedState(
        conferenceStatusFilteringOptionIds: Array<Long> = emptyArray(),
        conferenceTagFilteringOptionIds: Array<Long> = emptyArray(),
        conferenceStartDate: LocalDate?,
        conferenceEndDate: LocalDate?,
    ) {
        viewModelScope.launch {
            if (isAlreadyUpdatedFilterSelection) return@launch
            isAlreadyUpdatedFilterSelection = true

            fetchEventFilters()

            val conferenceFilter = _conferenceFilter.value
            val newConferenceFilter = conferenceFilter.copy(
                conferenceStatusFilteringOptions = conferenceFilter.conferenceStatusFilteringOptions.map {
                    it.copy(isSelected = conferenceStatusFilteringOptionIds.contains(it.id))
                },
                conferenceTagFilteringOptions = conferenceFilter.conferenceTagFilteringOptions.map {
                    it.copy(isSelected = conferenceTagFilteringOptionIds.contains(it.id))
                },
                selectedStartDate = conferenceStartDate?.let {
                    ConferenceFilteringDateOptionUiState(it)
                },
                selectedEndDate = conferenceEndDate?.let { ConferenceFilteringDateOptionUiState(it) },
            )
            _conferenceFilter.value = newConferenceFilter
        }
    }

    fun updateStartDate(startDate: LocalDate) {
        val filterDate = ConferenceFilteringDateOptionUiState(startDate)
        val endDate = _conferenceFilter.value.selectedEndDate
        val isAfterThanEndDate = endDate?.run { startDate.isAfter(date) } == true

        if (isAfterThanEndDate) {
            _conferenceFilter.value = _conferenceFilter.value.copy(
                selectedStartDate = filterDate,
                selectedEndDate = null,
            )
            return
        }

        _conferenceFilter.value = _conferenceFilter.value.copy(selectedStartDate = filterDate)
    }

    fun updateEndDate(endDate: LocalDate) {
        val isEqualsOrBeforeThanStartDate =
            selectedStartDate?.run { endDate.isEqual(date) || endDate.isBefore(date) } == true
        if (isEqualsOrBeforeThanStartDate) return

        val filterDate = ConferenceFilteringDateOptionUiState(endDate)
        _conferenceFilter.value = _conferenceFilter.value.copy(selectedEndDate = filterDate)
    }

    fun clearFilters() {
        _conferenceFilter.value = _conferenceFilter.value.resetSelection()
    }

    companion object {
        val factory = ViewModelFactory {
            ConferenceFilterViewModel(
                conferenceStatusRepository = KerdyApplication.repositoryContainer.conferenceStatusRepository,
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository,
            )
        }
    }
}
