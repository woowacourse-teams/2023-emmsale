package com.emmsale.presentation.ui.eventSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.data.network.callAdapter.Failure
import com.emmsale.data.network.callAdapter.NetworkError
import com.emmsale.data.network.callAdapter.Success
import com.emmsale.data.network.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.repository.interfaces.EventSearchRepository
import com.emmsale.model.Event
import com.emmsale.model.EventSearchHistory
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.ui.eventSearch.uistate.EventSearchResultsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventSearchViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val eventSearchRepository: EventSearchRepository,
) : ViewModel() {
    val eventSearchQuery: MutableStateFlow<String> = MutableStateFlow(INITIAL_QUERY)
    val eventSearchResults: LiveData<EventSearchResultsUiState> = eventSearchQuery
        .debounce(SEARCH_DEBOUNCE_TIME)
        .filter { query -> query.isNotBlank() }
        .map { query -> query.trim() }
        .distinctUntilChanged()
        .mapLatest { query -> eventRepository.searchEvents(query).toUiState() }
        .asLiveData()

    private val _eventSearchHistories = MutableLiveData<List<EventSearchHistory>>()
    val eventSearchHistories: LiveData<List<EventSearchHistory>> = _eventSearchHistories

    private fun ApiResponse<List<Event>>.toUiState(): EventSearchResultsUiState = when (this) {
        is Success -> EventSearchResultsUiState(data, FetchResult.SUCCESS)
        is Failure, is Unexpected -> EventSearchResultsUiState(emptyList(), FetchResult.ERROR)
        is NetworkError -> EventSearchResultsUiState(emptyList(), FetchResult.ERROR)
    }

    init {
        getAllEventSearchHistories()
    }

    fun searchEvents(searchQuery: String) {
        this.eventSearchQuery.value = searchQuery
    }

    fun saveEventSearch() {
        viewModelScope.launch {
            eventSearchRepository.save(eventSearchQuery.value)
            getAllEventSearchHistories()
        }
    }

    private fun getAllEventSearchHistories() {
        viewModelScope.launch {
            _eventSearchHistories.value = eventSearchRepository.getAll()
        }
    }

    fun deleteSearchHistory(eventSearch: EventSearchHistory) {
        viewModelScope.launch {
            eventSearchRepository.delete(eventSearch)
            getAllEventSearchHistories()
        }
    }

    fun deleteAllSearchHistory() {
        viewModelScope.launch {
            eventSearchRepository.deleteAll()
            getAllEventSearchHistories()
        }
    }

    companion object {
        private const val INITIAL_QUERY = ""
        private const val SEARCH_DEBOUNCE_TIME = 500L
    }
}
