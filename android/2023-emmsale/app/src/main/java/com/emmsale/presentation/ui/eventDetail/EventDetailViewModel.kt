package com.emmsale.presentation.ui.eventdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.model.EventDetail
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.data.repository.interfaces.ScrappedEventRepository
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.FetchResult.ERROR
import com.emmsale.presentation.common.FetchResult.LOADING
import com.emmsale.presentation.common.FetchResult.SUCCESS
import com.emmsale.presentation.common.firebase.analytics.logEventClick
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailScreenUiState
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailUiState
import com.emmsale.presentation.ui.eventDetailInfo.uiState.EventInfoUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val eventRepository: EventRepository,
    private val scrappedEventRepository: ScrappedEventRepository,
    private val recruitmentRepository: RecruitmentRepository,
) : ViewModel(), Refreshable {
    val eventId = stateHandle[EVENT_ID_KEY] ?: DEFAULT_EVENT_ID

    private val _eventDetail: NotNullMutableLiveData<EventDetailUiState> =
        NotNullMutableLiveData(EventDetailUiState())
    val eventDetail: NotNullLiveData<EventDetailUiState> = _eventDetail

    private val _scrapUiEvent = MutableLiveData<Event<EventInfoUiEvent>>()
    val scrapUiEvent: LiveData<Event<EventInfoUiEvent>> = _scrapUiEvent

    private val _isScraped: MutableLiveData<Boolean> = MutableLiveData(false)
    val isScraped: LiveData<Boolean> = _isScraped

    private val _currentScreen = NotNullMutableLiveData(EventDetailScreenUiState.INFORMATION)
    val currentScreen: NotNullLiveData<EventDetailScreenUiState> = _currentScreen

    private val _hasWritingPermission: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val hasWritingPermission: LiveData<Event<Boolean>> = _hasWritingPermission

    init {
        refresh()
    }

    override fun refresh() {
        changeToLoadingState()
        fetchEventDetail()
        fetchIsScrapped()
    }

    private fun fetchEventDetail() {
        viewModelScope.launch {
            when (val eventFetchResult = eventRepository.getEventDetail(eventId)) {
                is Success -> changeToSuccessState(eventFetchResult.data)
                is Failure, NetworkError, is Unexpected -> changeToErrorState()
            }
        }
    }

    fun fetchCurrentScreen(position: Int) {
        _currentScreen.value = EventDetailScreenUiState.from(position)
    }

    private fun fetchIsScrapped() {
        viewModelScope.launch {
            when (val isScrappedFetchResult = scrappedEventRepository.isScraped(eventId)) {
                is Success -> _isScraped.value = isScrappedFetchResult.data
                is Failure, NetworkError, is Unexpected -> {}
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
                else -> _scrapUiEvent.value = Event(EventInfoUiEvent.SCRAP_ERROR)
            }
        }
    }

    private fun deleteScrap() {
        viewModelScope.launch {
            when (scrappedEventRepository.deleteScrap(eventId = eventId)) {
                is Success -> _isScraped.value = false
                else -> _scrapUiEvent.value = Event(EventInfoUiEvent.SCRAP_DELETE_ERROR)
            }
        }
    }

    private fun changeToSuccessState(eventDetail: EventDetail) {
        _eventDetail.value =
            _eventDetail.value.copy(fetchResult = SUCCESS, eventDetail = eventDetail)
        logEventClick(eventDetail.name, eventDetail.id)
    }

    private fun changeToLoadingState() {
        _eventDetail.value = _eventDetail.value.copy(fetchResult = LOADING)
    }

    private fun changeToErrorState() {
        _eventDetail.value = _eventDetail.value.copy(fetchResult = ERROR)
    }

    fun fetchHasWritingPermission() {
        viewModelScope.launch {
            when (val response = recruitmentRepository.checkIsAlreadyPostRecruitment(eventId)) {
                is Success -> setHasPermissionWritingState(!response.data)
                else -> setHasPermissionWritingState(false)
            }
        }
    }

    private fun setHasPermissionWritingState(state: Boolean) {
        _hasWritingPermission.value = Event(state)
    }

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_EVENT_ID = -1L
    }
}
