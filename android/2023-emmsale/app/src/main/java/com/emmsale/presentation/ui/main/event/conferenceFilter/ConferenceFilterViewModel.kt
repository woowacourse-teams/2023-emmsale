package com.emmsale.presentation.ui.main.event.conferenceFilter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.conference.EventCategory
import com.emmsale.data.eventTag.EventTagRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterDateUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterStatusUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterTagUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ConferenceFilterViewModel(
    private val eventTagRepository: EventTagRepository,
    private val selectedStartDate: ConferenceFilterDateUiState = ConferenceFilterDateUiState(
        LocalDateTime.now().year, LocalDateTime.now().month.value
    ),
    private val selectedEndDate: ConferenceFilterDateUiState = ConferenceFilterDateUiState(
        LocalDateTime.now().year, LocalDateTime.now().month.value
    ),
) : ViewModel() {
    private val _eventFilters = MutableLiveData<ConferenceFilterUiState>()
    val eventFilters: LiveData<ConferenceFilterUiState> = _eventFilters

    init {
        viewModelScope.launch {
            _eventFilters.postValue(ConferenceFilterUiState.Loading)

            val statuses = fetchConferenceStatuses()
            val tags = fetchConferenceTags()

            _eventFilters.postValue(
                ConferenceFilterUiState.Success(
                    statuses = statuses,
                    tags = tags,
                    selectedStartDate = selectedStartDate,
                    selectedEndDate = selectedEndDate,
                )
            )
        }
    }

    private fun fetchConferenceStatuses(): List<ConferenceFilterStatusUiState> =
        listOf("컨퍼런스", "대회").map { conferenceName ->
            ConferenceFilterStatusUiState(conferenceName)
        }

    private suspend fun fetchConferenceTags(): List<ConferenceFilterTagUiState> =
        withContext(Dispatchers.IO) {
            when (val eventTagResult = eventTagRepository.getEventTags(EventCategory.CONFERENCE)) {
                is ApiSuccess -> eventTagResult.data.map { tag ->
                    ConferenceFilterTagUiState(id = tag.id, name = tag.name)
                }

                is ApiError,
                is ApiException,
                -> {
                    _eventFilters.postValue(ConferenceFilterUiState.Error)
                    emptyList()
                }
            }
        }

    companion object {
        val factory = ViewModelFactory {
            ConferenceFilterViewModel(
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository
            )
        }
    }
}
