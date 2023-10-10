package com.emmsale.presentation.ui.eventSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.model.Event
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.ui.eventSearch.uistate.EventSearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class EventSearchViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : ViewModel() {
    private val eventSearchQuery = MutableStateFlow(INITIAL_QUERY)
    val eventSearchResults: LiveData<EventSearchUiState> = eventSearchQuery
        .debounce(SEARCH_DEBOUNCE_TIME)
        .filter { query -> query.trim().isNotEmpty() }
        .map { query -> query.trim() }
        .distinctUntilChanged()
        .mapLatest { query -> eventRepository.searchEvents(query).toUiState() }
        .asLiveData()

    private fun ApiResponse<List<Event>>.toUiState(): EventSearchUiState = when (this) {
        is Success -> EventSearchUiState(data, FetchResult.SUCCESS)
        is Failure, is Unexpected -> EventSearchUiState(emptyList(), FetchResult.ERROR)
        is NetworkError -> EventSearchUiState(emptyList(), FetchResult.ERROR)
    }

    fun searchEvents(searchQuery: String) {
        this.eventSearchQuery.value = searchQuery
    }

    companion object {
        private const val INITIAL_QUERY = ""
        private const val SEARCH_DEBOUNCE_TIME = 500L
    }
}
