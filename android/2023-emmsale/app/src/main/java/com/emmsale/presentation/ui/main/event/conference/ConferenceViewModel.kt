package com.emmsale.presentation.ui.main.event.conference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.conferenceStatus.ConferenceStatus
import com.emmsale.data.conferenceStatus.ConferenceStatusRepository
import com.emmsale.data.event.EventCategory
import com.emmsale.data.event.EventRepository
import com.emmsale.data.event.model.Conference
import com.emmsale.data.eventTag.EventTag
import com.emmsale.data.eventTag.EventTagRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferenceSelectedFilteringDateOptionUiState
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferenceSelectedFilteringOptionUiState
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferenceSelectedFilteringUiState
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferenceUiState
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferencesUiState
import kotlinx.coroutines.launch
import java.time.LocalDate

class ConferenceViewModel(
    private val eventRepository: EventRepository,
    private val conferenceStatusRepository: ConferenceStatusRepository,
    private val eventTagRepository: EventTagRepository,
) : ViewModel() {
    private val _conferences = NotNullMutableLiveData(ConferencesUiState())
    val conferences: NotNullLiveData<ConferencesUiState> = _conferences

    private val _selectedFilter = NotNullMutableLiveData(ConferenceSelectedFilteringUiState())
    val selectedFilter: NotNullLiveData<ConferenceSelectedFilteringUiState> = _selectedFilter

    init {
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
            when (val eventsResult = getConferences(statuses, tags, startDate, endDate)) {
                is ApiSuccess ->
                    _conferences.value = _conferences.value.copy(
                        conferences = eventsResult.data.map(ConferenceUiState::from),
                        isLoading = false,
                    )

                is ApiError, is ApiException -> _conferences.value = _conferences.value.copy(
                    isLoadingConferencesFailed = true,
                    isLoading = false,
                )
            }
        }
    }

    private suspend fun getConferences(
        statuses: List<ConferenceStatus>,
        tags: List<EventTag>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): ApiResult<List<Conference>> = eventRepository.getConferences(
        statuses = statuses,
        tags = tags,
        startDate = startDate,
        endDate = endDate,
    )

    private fun fetchFilteredConferences(filterOption: ConferenceSelectedFilteringUiState) {
        with(filterOption) {
            fetchFilteredConferences(
                selectedStatusFilteringOptionIds,
                selectedTagFilteringOptionIds,
                startDateFilteringOption?.date,
                endDateFilteringOption?.date,
            )
        }
    }

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
        when (
            val eventTagResult =
                eventTagRepository.getEventTagByIds(EventCategory.CONFERENCE, statusFilterIds)
        ) {
            is ApiSuccess -> eventTagResult.data
            is ApiError, is ApiException -> emptyList()
        }

    fun removeFilteringOptionBy(filterOptionId: Long) {
        val newSelectedFilter = _selectedFilter.value.removeFilteringOptionBy(filterOptionId)
        _selectedFilter.value = newSelectedFilter

        fetchFilteredConferences(newSelectedFilter)
    }

    fun removeDurationFilteringOption() {
        _selectedFilter.value = _selectedFilter.value.clearSelectedDate()
    }

    companion object {
        val factory = ViewModelFactory {
            ConferenceViewModel(
                conferenceStatusRepository = KerdyApplication.repositoryContainer.conferenceStatusRepository,
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository,
                eventRepository = KerdyApplication.repositoryContainer.eventRepository,
            )
        }
    }
}
