package com.emmsale.presentation.ui.conferenceList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.model.Conference
import com.emmsale.data.model.ConferenceStatus
import com.emmsale.data.model.EventTag
import com.emmsale.data.repository.interfaces.ConferenceStatusRepository
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.repository.interfaces.EventTagRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.conferenceList.uiState.ConferenceSelectedFilteringDateOptionUiState
import com.emmsale.presentation.ui.conferenceList.uiState.ConferenceSelectedFilteringOptionUiState
import com.emmsale.presentation.ui.conferenceList.uiState.ConferenceSelectedFilteringUiState
import com.emmsale.presentation.ui.conferenceList.uiState.ConferenceUiState
import com.emmsale.presentation.ui.conferenceList.uiState.ConferencesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ConferenceViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val conferenceStatusRepository: ConferenceStatusRepository,
    private val eventTagRepository: EventTagRepository,
) : ViewModel(), Refreshable {
    private val _conferences = NotNullMutableLiveData(ConferencesUiState())
    val conferences: NotNullLiveData<ConferencesUiState> = _conferences

    private val _selectedFilter = NotNullMutableLiveData(ConferenceSelectedFilteringUiState())
    val selectedFilter: NotNullLiveData<ConferenceSelectedFilteringUiState> = _selectedFilter

    init {
        refresh()
    }

    override fun refresh() {
        _selectedFilter.value = ConferenceSelectedFilteringUiState()
        fetchConferences()
    }

    private fun fetchConferences(
        statuses: List<ConferenceStatus> = emptyList(),
        tags: List<EventTag> = emptyList(),
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ) {
        viewModelScope.launch {
            _conferences.value = _conferences.value.copy(isLoading = true)
            when (val result = getConferences(statuses, tags, startDate, endDate)) {
                is Failure, NetworkError -> _conferences.value = _conferences.value.copy(
                    isError = true,
                    isLoading = false,
                )

                is Success -> _conferences.value = _conferences.value.copy(
                    conferences = result.data.map(ConferenceUiState::from),
                    isLoading = false,
                    isError = false,
                )

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    private suspend fun getConferences(
        statuses: List<ConferenceStatus>,
        tags: List<EventTag>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): ApiResponse<List<Conference>> = eventRepository.getConferences(
        statuses = statuses,
        tags = tags,
        startDate = startDate,
        endDate = endDate,
    )

    fun fetchFilteredConferences(
        statusFilterIds: Array<Long>,
        tagFilterIds: Array<Long>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ) {
        viewModelScope.launch {
            val statusFilteringOptions = getConferenceStatusByIds(statusFilterIds)
            val tagFilteringOptions = getEventTagByIds(tagFilterIds)

            fetchConferences(
                statuses = statusFilteringOptions,
                tags = tagFilteringOptions,
                startDate = startDate,
                endDate = endDate,
            )
            updateSelectedFilter(statusFilteringOptions, tagFilteringOptions, startDate, endDate)
        }
    }

    private fun updateSelectedFilter(
        statusFilteringOptions: List<ConferenceStatus>,
        tagFilteringOptions: List<EventTag>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ) {
        _selectedFilter.postValue(
            _selectedFilter.value.copy(
                statusFilteringOptions = statusFilteringOptions.map(
                    ConferenceSelectedFilteringOptionUiState::from,
                ),
                tagFilteringOptions = tagFilteringOptions.map(
                    ConferenceSelectedFilteringOptionUiState::from,
                ),
                startDateFilteringOption = startDate?.let(
                    ConferenceSelectedFilteringDateOptionUiState::from,
                ),
                endDateFilteringOption = endDate?.let(ConferenceSelectedFilteringDateOptionUiState::from),
            ),
        )
    }

    private suspend fun getConferenceStatusByIds(tagFilterIds: Array<Long>): List<ConferenceStatus> =
        conferenceStatusRepository.getConferenceStatusByIds(tagFilterIds)

    private suspend fun getEventTagByIds(statusFilterIds: Array<Long>): List<EventTag> =
        when (val result = eventTagRepository.getEventTagByIds(statusFilterIds)) {
            is Success -> result.data
            is Failure, NetworkError -> {
                _conferences.value = _conferences.value.copy(
                    isError = true,
                    isLoading = false,
                )
                emptyList()
            }

            is Unexpected -> throw Throwable(result.error)
        }

    fun removeFilteringOptionBy(filterOptionId: Long) {
        _selectedFilter.value = selectedFilter.value.removeFilteringOptionBy(filterOptionId)
        fetchFilteredConferences()
    }

    fun removeDurationFilteringOption() {
        _selectedFilter.value = selectedFilter.value.clearSelectedDate()
        fetchFilteredConferences()
    }

    private fun fetchFilteredConferences() {
        with(selectedFilter.value) {
            fetchFilteredConferences(
                selectedStatusFilteringOptionIds,
                selectedTagFilteringOptionIds,
                startDateFilteringOption?.date,
                endDateFilteringOption?.date,
            )
        }
    }
}