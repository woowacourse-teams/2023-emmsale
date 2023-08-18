package com.emmsale.presentation.ui.main.event.competition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.competitionStatus.CompetitionStatus
import com.emmsale.data.competitionStatus.CompetitionStatusRepository
import com.emmsale.data.event.EventCategory
import com.emmsale.data.event.EventRepository
import com.emmsale.data.event.model.Competition
import com.emmsale.data.eventTag.EventTag
import com.emmsale.data.eventTag.EventTagRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.RefreshableViewModel
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.main.event.competition.uistate.CompetitionSelectedFilteringDateOptionUiState
import com.emmsale.presentation.ui.main.event.competition.uistate.CompetitionSelectedFilteringOptionUiState
import com.emmsale.presentation.ui.main.event.competition.uistate.CompetitionSelectedFilteringUiState
import com.emmsale.presentation.ui.main.event.competition.uistate.CompetitionUiState
import com.emmsale.presentation.ui.main.event.competition.uistate.CompetitionsUiState
import kotlinx.coroutines.launch
import java.time.LocalDate

class CompetitionViewModel(
    private val eventRepository: EventRepository,
    private val competitionStatusRepository: CompetitionStatusRepository,
    private val eventTagRepository: EventTagRepository,
) : ViewModel(), RefreshableViewModel {
    private val _competitions = NotNullMutableLiveData(CompetitionsUiState())
    val competitions: NotNullLiveData<CompetitionsUiState> = _competitions

    private val _selectedFilter = NotNullMutableLiveData(CompetitionSelectedFilteringUiState())
    val selectedFilter: NotNullLiveData<CompetitionSelectedFilteringUiState> = _selectedFilter

    init {
        refresh()
    }

    override fun refresh() {
        _selectedFilter.value = CompetitionSelectedFilteringUiState()
        fetchCompetitions()
    }

    private fun fetchCompetitions(
        statuses: List<CompetitionStatus> = emptyList(),
        tags: List<EventTag> = emptyList(),
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ) {
        viewModelScope.launch {
            _competitions.value = _competitions.value.copy(isLoading = true)
            when (val eventsResult = getCompetitions(statuses, tags, startDate, endDate)) {
                is ApiSuccess ->
                    _competitions.value = _competitions.value.copy(
                        competitions = eventsResult.data.map(CompetitionUiState::from),
                        isLoading = false,
                        isError = false,
                    )

                is ApiError, is ApiException -> _competitions.value = _competitions.value.copy(
                    isError = true,
                    isLoading = false,
                )
            }
        }
    }

    private suspend fun getCompetitions(
        statuses: List<CompetitionStatus>,
        tags: List<EventTag>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): ApiResult<List<Competition>> = eventRepository.getCompetitions(
        statuses = statuses,
        tags = tags,
        startDate = startDate,
        endDate = endDate,
    )

    private fun fetchFilteredCompetitions(filterOption: CompetitionSelectedFilteringUiState) {
        with(filterOption) {
            fetchFilteredCompetitions(
                selectedStatusFilteringOptionIds,
                selectedTagFilteringOptionIds,
                selectedStartDate?.date,
                selectedEndDate?.date,
            )
        }
    }

    fun fetchFilteredCompetitions(
        statusFilterIds: Array<Long>,
        tagFilterIds: Array<Long>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ) {
        viewModelScope.launch {
            val statusFilteringOptions = getCompetitionStatusByIds(statusFilterIds)
            val tagFilteringOptions = getEventTagByIds(tagFilterIds)

            fetchCompetitions(
                statuses = statusFilteringOptions,
                tags = tagFilteringOptions,
                startDate = startDate,
                endDate = endDate,
            )
            updateSelectedFilter(statusFilteringOptions, tagFilteringOptions, startDate, endDate)
        }
    }

    private fun updateSelectedFilter(
        statusFilteringOptions: List<CompetitionStatus>,
        tagFilteringOptions: List<EventTag>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ) {
        _selectedFilter.postValue(
            _selectedFilter.value.copy(
                competitionStatusFilteringOptions = statusFilteringOptions.map(
                    CompetitionSelectedFilteringOptionUiState::from,
                ),
                competitionTagFilteringOptions = tagFilteringOptions.map(
                    CompetitionSelectedFilteringOptionUiState::from,
                ),
                selectedStartDate = startDate?.let(CompetitionSelectedFilteringDateOptionUiState::from),
                selectedEndDate = endDate?.let(CompetitionSelectedFilteringDateOptionUiState::from),
            ),
        )
    }

    private suspend fun getCompetitionStatusByIds(tagFilterIds: Array<Long>): List<CompetitionStatus> =
        competitionStatusRepository.getCompetitionStatusByIds(tagFilterIds)

    private suspend fun getEventTagByIds(statusFilterIds: Array<Long>): List<EventTag> =
        when (
            val eventTagResult =
                eventTagRepository.getEventTagByIds(EventCategory.COMPETITION, statusFilterIds)
        ) {
            is ApiSuccess -> eventTagResult.data
            is ApiError, is ApiException -> emptyList()
        }

    fun removeFilteringOptionBy(filterOptionId: Long) {
        val newSelectedFilter = _selectedFilter.value.removeFilteringOptionBy(filterOptionId)
        _selectedFilter.value = newSelectedFilter

        fetchFilteredCompetitions(newSelectedFilter)
    }

    fun removeDurationFilteringOption() {
        _selectedFilter.value = _selectedFilter.value.clearSelectedDate()
    }

    companion object {
        val factory = ViewModelFactory {
            CompetitionViewModel(
                competitionStatusRepository = KerdyApplication.repositoryContainer.competitionStatusRepository,
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository,
                eventRepository = KerdyApplication.repositoryContainer.eventRepository,
            )
        }
    }
}
