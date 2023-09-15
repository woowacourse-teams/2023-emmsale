package com.emmsale.presentation.ui.eventDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.model.EventDetail
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.repository.interfaces.ScrappedEventRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.FetchResult.ERROR
import com.emmsale.presentation.common.FetchResult.LOADING
import com.emmsale.presentation.common.FetchResult.SUCCESS
import com.emmsale.presentation.common.firebase.analytics.logEventClick
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailUiState
import com.emmsale.presentation.ui.eventDetailInfo.uiState.EventInfoUiEvent
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val eventId: Long,
    private val eventRepository: EventRepository,
    private val scrappedEventRepository: ScrappedEventRepository,
) : ViewModel(), Refreshable {

    private val _eventDetail: NotNullMutableLiveData<EventDetailUiState> =
        NotNullMutableLiveData(EventDetailUiState())
    val eventDetail: NotNullLiveData<EventDetailUiState> = _eventDetail

    private val _scrapUiEvent: MutableLiveData<EventInfoUiEvent?> = MutableLiveData(null)
    val scrapUiEvent: LiveData<EventInfoUiEvent?> = _scrapUiEvent

    private val _isScraped: MutableLiveData<Boolean> = MutableLiveData(false)
    val isScraped: LiveData<Boolean> = _isScraped

    init {
        refresh()
    }

    override fun refresh() {
        changeToLoadingState()
        viewModelScope.launch {
            when (val eventFetchResult = eventRepository.getEventDetail(eventId)) {
                is Success -> fetchSuccessEventDetail(eventFetchResult.data)
                is Failure, NetworkError, is Unexpected -> changeToErrorState()
            }

            when (val isScrappedFetchResult = scrappedEventRepository.isScraped(eventId)) {
                is Success -> _isScraped.value = isScrappedFetchResult.data
                is Failure, NetworkError, is Unexpected -> changeToErrorState()
            }
        }
    }

    fun handleEventScrap() {
        when (isScraped.value) {
            null, true -> deleteScrap()
            false -> scrapEvent()
        }
    }

    private fun scrapEvent() {
        viewModelScope.launch {
            when (scrappedEventRepository.scrapEvent(eventId = eventId)) {
                is Success -> _isScraped.value = true
                else -> _scrapUiEvent.value = EventInfoUiEvent.SCRAP_ERROR
            }
        }
    }

    private fun deleteScrap() {
        viewModelScope.launch {
            when (scrappedEventRepository.deleteScrap(eventId = eventId)) {
                is Success -> _isScraped.value = false
                else -> _scrapUiEvent.value = EventInfoUiEvent.SCRAP_DELETE_ERROR
            }
        }
    }

    private fun fetchSuccessEventDetail(eventDetail: EventDetail) {
        _eventDetail.value = EventDetailUiState(SUCCESS, eventDetail)
        logEventClick(eventDetail.name, eventDetail.id)
    }

    private fun changeToLoadingState() {
        _eventDetail.value = eventDetail.value.copy(fetchResult = LOADING)
    }

    private fun changeToErrorState() {
        _eventDetail.value = eventDetail.value.copy(fetchResult = ERROR)
    }

    companion object {
        fun factory(eventId: Long) = ViewModelFactory {
            EventDetailViewModel(
                eventId,
                eventRepository = KerdyApplication.repositoryContainer.eventRepository,
                scrappedEventRepository = KerdyApplication.repositoryContainer.scrappedEventRepository,
            )
        }
    }
}
