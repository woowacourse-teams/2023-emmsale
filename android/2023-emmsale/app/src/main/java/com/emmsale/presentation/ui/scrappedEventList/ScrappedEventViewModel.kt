package com.emmsale.presentation.ui.scrappedEventList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.scrappedEventList.uiState.ScrappedEventsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScrappedEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : ViewModel(), Refreshable {
    private val _scrappedEvents = NotNullMutableLiveData(ScrappedEventsUiState())
    val scrappedEvents: NotNullLiveData<ScrappedEventsUiState> = _scrappedEvents

    override fun refresh() {
        changeToLoadingState()
        viewModelScope.launch {
            when (val response = eventRepository.getScrappedEvents()) {
                is Success -> _scrappedEvents.value = ScrappedEventsUiState.from(response.data)
                else -> changeToErrorState()
            }
        }
    }

    private fun changeToLoadingState() {
        _scrappedEvents.value = ScrappedEventsUiState(fetchResult = FetchResult.LOADING)
    }

    private fun changeToErrorState() {
        _scrappedEvents.value =
            ScrappedEventsUiState(fetchResult = FetchResult.ERROR)
    }
}
