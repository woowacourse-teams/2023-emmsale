package com.emmsale.presentation.ui.competitionFilter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.CompetitionStatusRepository
import com.emmsale.data.repository.interfaces.EventTagRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.competitionFilter.uiState.CompetitionFilterUiState
import com.emmsale.presentation.ui.competitionFilter.uiState.CompetitionFilteringDateOptionUiState
import com.emmsale.presentation.ui.competitionFilter.uiState.CompetitionFilteringOptionUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class CompetitionFilterViewModel(
    private val eventTagRepository: EventTagRepository,
    private val competitionStatusRepository: CompetitionStatusRepository,
    private val selectedStartDate: CompetitionFilteringDateOptionUiState? = null,
    private val selectedEndDate: CompetitionFilteringDateOptionUiState? = null,
) : ViewModel() {
    private val _competitionFilter = NotNullMutableLiveData(CompetitionFilterUiState())
    val competitionFilter: NotNullLiveData<CompetitionFilterUiState> = _competitionFilter

    val isTagAllSelected: LiveData<Boolean> = _competitionFilter.map { filter ->
        val tagFilterSize = filter.tagFilteringOptions.size
        val selectedTagFilterSize = filter.tagFilteringOptions.count { it.isSelected }
        tagFilterSize == selectedTagFilterSize
    }
    val isStartDateSelected: LiveData<Boolean> = _competitionFilter.map { filters ->
        filters.selectedStartDate != null
    }
    private var isAlreadyUpdatedFilterSelection = false

    private suspend fun fetchEventFilters() {
        _competitionFilter.value = _competitionFilter.value.copy(isLoading = true)

        val statuses = fetchCompetitionStatuses()
        val tags = fetchCompetitionTags()

        _competitionFilter.value = CompetitionFilterUiState(
            statusFilteringOptions = statuses,
            tagFilteringOptions = tags,
            selectedStartDate = selectedStartDate,
            selectedEndDate = selectedEndDate,
            isLoading = false,
            isLoadingCompetitionFilterFailed = false,
        )
    }

    private suspend fun fetchCompetitionTags(): List<CompetitionFilteringOptionUiState> =
        withContext(Dispatchers.IO) {
            when (val result = eventTagRepository.getEventTags()) {
                is Success -> result.data.map(CompetitionFilteringOptionUiState::from)
                is Unexpected, is Failure, NetworkError -> {
                    _competitionFilter.value =
                        _competitionFilter.value.copy(isLoadingCompetitionFilterFailed = true)
                    emptyList()
                }
            }
        }

    private suspend fun fetchCompetitionStatuses(): List<CompetitionFilteringOptionUiState> =
        competitionStatusRepository.getCompetitionStatuses()
            .map(CompetitionFilteringOptionUiState::from)

    fun toggleFilterSelection(filter: CompetitionFilteringOptionUiState) {
        _competitionFilter.value = _competitionFilter.value.toggleSelectionBy(filter.id)
    }

    fun updateFilteringOptionsToSelectedState(
        competitionStatusFilteringOptionIds: Array<Long> = emptyArray(),
        competitionTagFilteringOptionIds: Array<Long> = emptyArray(),
        competitionStartDate: LocalDate?,
        competitionEndDate: LocalDate?,
    ) {
        viewModelScope.launch {
            if (isAlreadyUpdatedFilterSelection) return@launch
            isAlreadyUpdatedFilterSelection = true

            fetchEventFilters()

            val competitionFilter = _competitionFilter.value
            val newCompetitionFilter = competitionFilter.copy(
                statusFilteringOptions = competitionFilter.statusFilteringOptions.map {
                    it.copy(isSelected = competitionStatusFilteringOptionIds.contains(it.id))
                },
                tagFilteringOptions = competitionFilter.tagFilteringOptions.map {
                    it.copy(isSelected = competitionTagFilteringOptionIds.contains(it.id))
                },
                selectedStartDate = competitionStartDate?.let {
                    CompetitionFilteringDateOptionUiState(it)
                },
                selectedEndDate = competitionEndDate?.let { CompetitionFilteringDateOptionUiState(it) },
            )
            _competitionFilter.value = newCompetitionFilter
        }
    }

    fun updateStartDate(startDate: LocalDate) {
        val filterDate = CompetitionFilteringDateOptionUiState(startDate)
        val endDate = _competitionFilter.value.selectedEndDate
        val isAfterThanEndDate = endDate?.run { startDate.isAfter(date) } == true

        if (isAfterThanEndDate) {
            _competitionFilter.value = _competitionFilter.value.copy(
                selectedStartDate = filterDate,
                selectedEndDate = null,
            )
            return
        }

        _competitionFilter.value = _competitionFilter.value.copy(selectedStartDate = filterDate)
    }

    fun updateEndDate(endDate: LocalDate) {
        val isEqualsOrBeforeThanStartDate =
            selectedStartDate?.run { endDate.isEqual(date) || endDate.isBefore(date) } == true
        if (isEqualsOrBeforeThanStartDate) return

        val filterDate = CompetitionFilteringDateOptionUiState(endDate)
        _competitionFilter.value = _competitionFilter.value.copy(selectedEndDate = filterDate)
    }

    fun clearFilters() {
        _competitionFilter.value = _competitionFilter.value.resetSelection()
    }

    companion object {
        val factory = ViewModelFactory {
            CompetitionFilterViewModel(
                competitionStatusRepository = KerdyApplication.repositoryContainer.competitionStatusRepository,
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository,
            )
        }
    }
}
