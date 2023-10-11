package com.emmsale.presentation.ui.conferenceFilter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.ConferenceStatusRepository
import com.emmsale.data.repository.interfaces.EventTagRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.conferenceFilter.uiState.ConferenceFilterUiState
import com.emmsale.presentation.ui.conferenceFilter.uiState.ConferenceFilteringDateOptionUiState
import com.emmsale.presentation.ui.conferenceFilter.uiState.ConferenceFilteringOptionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ConferenceFilterViewModel @Inject constructor(
    private val eventTagRepository: EventTagRepository,
    private val conferenceStatusRepository: ConferenceStatusRepository,
) : ViewModel() {
    private val selectedStartDate: ConferenceFilteringDateOptionUiState? = null
    private val selectedEndDate: ConferenceFilteringDateOptionUiState? = null

    private val _conferenceFilter = NotNullMutableLiveData(ConferenceFilterUiState())
    val conferenceFilter: NotNullLiveData<ConferenceFilterUiState> = _conferenceFilter

    val isTagAllSelected: LiveData<Boolean> = _conferenceFilter.map { filter ->
        val tagFilterSize = filter.tagFilteringOptions.size
        val selectedTagFilterSize = filter.tagFilteringOptions.count { it.isSelected }
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
            statusFilteringOptions = statuses,
            tagFilteringOptions = tags,
            selectedStartDate = selectedStartDate,
            selectedEndDate = selectedEndDate,
            isLoading = false,
            isLoadingConferenceFilterFailed = false,
        )
    }

    private suspend fun fetchConferenceTags(): List<ConferenceFilteringOptionUiState> =
        withContext(Dispatchers.IO) {
            when (val result = eventTagRepository.getEventTags()) {
                is Success -> result.data.map(ConferenceFilteringOptionUiState::from)
                is Failure, NetworkError -> {
                    _conferenceFilter.value =
                        _conferenceFilter.value.copy(isLoadingConferenceFilterFailed = true)
                    emptyList()
                }

                is Unexpected -> throw Throwable(result.error)
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
                statusFilteringOptions = conferenceFilter.statusFilteringOptions.map {
                    it.copy(isSelected = conferenceStatusFilteringOptionIds.contains(it.id))
                },
                tagFilteringOptions = conferenceFilter.tagFilteringOptions.map {
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
}
