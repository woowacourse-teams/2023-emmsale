package com.emmsale.presentation.ui.eventDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.emmsale.data.network.callAdapter.Success
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.model.Event
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.eventDetail.eventSharer.EventSharer
import com.emmsale.presentation.ui.eventDetail.eventSharer.EventTemplateMaker
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailScreenUiState
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val eventRepository: EventRepository,
    private val recruitmentRepository: RecruitmentRepository,
    private val eventTemplateMaker: EventTemplateMaker,
    private val eventSharer: EventSharer,
) : RefreshableViewModel() {
    var eventId = stateHandle[EVENT_ID_KEY] ?: DEFAULT_EVENT_ID

    private val _event = NotNullMutableLiveData(Event())
    val event: NotNullLiveData<Event> = _event

    private val _isScraped = NotNullMutableLiveData(false)
    val isScraped: NotNullLiveData<Boolean> = _isScraped

    private val _canChangeIsScrapped = NotNullMutableLiveData(true)
    val canChangeIsScrapped: NotNullLiveData<Boolean> = _canChangeIsScrapped

    private val _currentScreen = NotNullMutableLiveData(EventDetailScreenUiState.INFORMATION)
    val currentScreen: NotNullLiveData<EventDetailScreenUiState> = _currentScreen

    private val _canStartToWriteRecruitment = NotNullMutableLiveData(true)
    val canStartToWrite: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(_canStartToWriteRecruitment) { value = canStartToWrite() }
        addSource(_currentScreen) { value = canStartToWrite() }
    }

    private fun canStartToWrite(): Boolean =
        _currentScreen.value != EventDetailScreenUiState.RECRUITMENT || _canStartToWriteRecruitment.value

    private val _uiEvent = SingleLiveEvent<EventDetailUiEvent>()
    val uiEvent: LiveData<EventDetailUiEvent> = _uiEvent

    fun fetchEvent(): Job = fetchData(
        fetchData = { eventRepository.getEventDetail(eventId) },
        onSuccess = { _event.value = it },
    )

    fun fetchIsScrapped(): Job = viewModelScope.launch {
        when (val result = eventRepository.isScraped(eventId)) {
            is Success -> _isScraped.value = result.data
            else -> {}
        }
    }

    override fun refresh(): Job {
        fetchIsScrapped()
        return refreshEvent()
    }

    private fun refreshEvent(): Job = refreshData(
        refresh = { eventRepository.getEventDetail(eventId) },
        onSuccess = { _event.value = it },
    )

    fun fetchCurrentScreen(position: Int) {
        _currentScreen.value = EventDetailScreenUiState.from(position)
    }

    fun toggleIsScrapped(): Job = command(
        command = {
            if (isScraped.value) {
                eventRepository.scrapOffEvent(eventId)
            } else {
                eventRepository.scrapEvent(eventId)
            }
        },
        onSuccess = { _isScraped.value = !_isScraped.value },
        onFailure = { _, _ ->
            if (isScraped.value) {
                _uiEvent.value = EventDetailUiEvent.ScrapOffFail
            } else {
                _uiEvent.value = EventDetailUiEvent.ScrapFail
            }
        },
        onStart = { _canChangeIsScrapped.value = false },
        onFinish = { _canChangeIsScrapped.value = true },
    )

    fun checkIsAlreadyPostRecruitment(): Job = fetchData(
        fetchData = { recruitmentRepository.checkIsAlreadyPostRecruitment(eventId) },
        onSuccess = { isAlreadyPosted ->
            _uiEvent.value = if (isAlreadyPosted) {
                EventDetailUiEvent.RecruitmentIsAlreadyPosted
            } else {
                EventDetailUiEvent.RecruitmentPostApproval
            }
        },
        onFailure = { _, _ -> _uiEvent.value = EventDetailUiEvent.RecruitmentPostedCheckFail },
        onLoading = { delayLoading() },
        onStart = { _canStartToWriteRecruitment.value = false },
        onFinish = { _canStartToWriteRecruitment.value = true },
    )

    fun shareEvent() {
        val posterImageUrl = event.value.posterImageUrl
        if (posterImageUrl == null) {
            _uiEvent.value = EventDetailUiEvent.KakaoShareFail
            return
        }

        val eventShareTemplate = eventTemplateMaker.create(
            eventId = eventId,
            eventName = event.value.name,
            posterUrl = posterImageUrl,
        )
        eventSharer.shareEvent(eventShareTemplate)
    }

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_EVENT_ID = -1L
    }
}
