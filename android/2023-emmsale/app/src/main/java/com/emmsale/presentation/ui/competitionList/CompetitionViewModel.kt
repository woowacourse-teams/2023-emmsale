package com.emmsale.presentation.ui.competitionList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.model.Competition
import com.emmsale.data.model.CompetitionStatus
import com.emmsale.data.model.EventTag
import com.emmsale.data.repository.interfaces.CompetitionStatusRepository
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.repository.interfaces.EventTagRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.competitionList.uiState.CompetitionSelectedFilteringDateOptionUiState
import com.emmsale.presentation.ui.competitionList.uiState.CompetitionSelectedFilteringOptionUiState
import com.emmsale.presentation.ui.competitionList.uiState.CompetitionSelectedFilteringUiState
import com.emmsale.presentation.ui.competitionList.uiState.CompetitionUiState
import com.emmsale.presentation.ui.competitionList.uiState.CompetitionsUiState
import kotlinx.coroutines.launch
import java.time.LocalDate

class CompetitionViewModel(
    private val eventRepository: EventRepository,
    private val competitionStatusRepository: CompetitionStatusRepository,
    private val eventTagRepository: EventTagRepository,
) : ViewModel(), Refreshable {
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
            when (val result = getCompetitions(statuses, tags, startDate, endDate)) {
                is Failure, NetworkError -> _competitions.value = _competitions.value.copy(
                    isError = true,
                    isLoading = false,
                )

                is Success -> _competitions.value = _competitions.value.copy(
                    competitions = result.data.map(CompetitionUiState::from),
                    isLoading = false,
                    isError = false,
                )

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    private suspend fun getCompetitions(
        statuses: List<CompetitionStatus>,
        tags: List<EventTag>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): ApiResponse<List<Competition>> = eventRepository.getCompetitions(
        statuses = statuses,
        tags = tags,
        startDate = startDate,
        endDate = endDate,
    )

    private fun fetchFilteredCompetitions() {
        with(selectedFilter.value) {
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
        when (val result = eventTagRepository.getEventTagByIds(statusFilterIds)) {
            is Success -> result.data
            is Failure, NetworkError -> {
                _competitions.value = _competitions.value.copy(
                    isError = true,
                    isLoading = false,
                )
                emptyList()
            }

            is Unexpected -> throw Throwable(result.error)
        }

    fun removeFilteringOptionBy(filterOptionId: Long) {
        _selectedFilter.value = selectedFilter.value.removeFilteringOptionBy(filterOptionId)
        fetchFilteredCompetitions()
    }

    fun removeDurationFilteringOption() {
        _selectedFilter.value = _selectedFilter.value.clearSelectedDate()
        fetchFilteredCompetitions()
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
