package com.emmsale.presentation.ui.eventSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.model.Event
import com.emmsale.data.model.EventSearch
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.repository.interfaces.EventSearchRepository
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.ui.eventSearch.uistate.EventSearchUiState
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
    val eventSearchQuery = MutableStateFlow(INITIAL_QUERY)
    val eventSearchResults: LiveData<EventSearchUiState> = eventSearchQuery
        .debounce(SEARCH_DEBOUNCE_TIME)
        .filter { query -> query.isNotBlank() }
        .map { query -> query.trim() }
        .distinctUntilChanged()
        .mapLatest { query -> eventRepository.searchEvents(query).toUiState() }
        .asLiveData()

    private val _eventSearchHistories = MutableLiveData<List<EventSearch>>()
    val eventSearchHistories: LiveData<List<EventSearch>> = _eventSearchHistories

    private fun ApiResponse<List<Event>>.toUiState(): EventSearchUiState = when (this) {
        is Success -> EventSearchUiState(data, FetchResult.SUCCESS)
        is Failure, is Unexpected -> EventSearchUiState(emptyList(), FetchResult.ERROR)
        is NetworkError -> EventSearchUiState(emptyList(), FetchResult.ERROR)
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

    fun deleteSearchHistory(eventSearch: EventSearch) {
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
