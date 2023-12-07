package com.emmsale.presentation.ui.eventDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.model.Event
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailScreenUiState
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val eventRepository: EventRepository,
    private val recruitmentRepository: RecruitmentRepository,
) : RefreshableViewModel() {
    val eventId = stateHandle[EVENT_ID_KEY] ?: DEFAULT_EVENT_ID

    private val _event = NotNullMutableLiveData(Event())
    val event: NotNullLiveData<Event> = _event

    private val _isScraped = NotNullMutableLiveData(false)
    val isScraped: NotNullLiveData<Boolean> = _isScraped

    var isAlreadyRecruitmentPostWritten = false
        private set

    private var canChangeIsScrapped = true

    private val _currentScreen = NotNullMutableLiveData(EventDetailScreenUiState.INFORMATION)
    val currentScreen: NotNullLiveData<EventDetailScreenUiState> = _currentScreen

    private val _uiEvent = SingleLiveEvent<EventDetailUiEvent>()
    val uiEvent: LiveData<EventDetailUiEvent> = _uiEvent

    init {
        fetchEvent()
        fetchIsScrapped()
        fetchIsAlreadyRecruitmentPostWritten()
    }

    private fun fetchEvent(): Job = fetchData(
        fetchData = { eventRepository.getEventDetail(eventId) },
        onSuccess = { _event.value = it },
    )

    private fun fetchIsScrapped(): Job = fetchData(
        fetchData = { eventRepository.isScraped(eventId) },
        onSuccess = { _isScraped.value = it },
        onLoading = {},
    )

    private fun fetchIsAlreadyRecruitmentPostWritten(): Job = fetchData(
        fetchData = { recruitmentRepository.checkIsAlreadyPostRecruitment(eventId) },
        onSuccess = { isAlreadyRecruitmentPostWritten = it },
        onLoading = {},
    )

    override fun refresh(): Job {
        refreshIsScrapped()
        refreshIsAlreadyRecruitmentPostWritten()
        return refreshEvent()
    }

    private fun refreshEvent(): Job = refreshData(
        refresh = { eventRepository.getEventDetail(eventId) },
        onSuccess = { _event.value = it },
    )

    private fun refreshIsScrapped(): Job = refreshData(
        refresh = { eventRepository.isScraped(eventId) },
        onSuccess = { _isScraped.value = it },
    )

    private fun refreshIsAlreadyRecruitmentPostWritten(): Job = refreshData(
        refresh = { recruitmentRepository.checkIsAlreadyPostRecruitment(eventId) },
        onSuccess = { isAlreadyRecruitmentPostWritten = it },
    )

    fun fetchCurrentScreen(position: Int) {
        _currentScreen.value = EventDetailScreenUiState.from(position)
    }

    fun toggleIsScrapped(): Job = command(
        command = {
            if (!canChangeIsScrapped) return@command Failure(MEANINGLESS_CODE, "")
            if (isScraped.value) {
                eventRepository.scrapOffEvent(eventId)
            } else {
                eventRepository.scrapEvent(eventId)
            }
        },
        onSuccess = { _isScraped.value = !_isScraped.value },
        onFailure = { code, _ ->
            if (code == MEANINGLESS_CODE) return@command

            if (isScraped.value) {
                _uiEvent.value = EventDetailUiEvent.ScrapOffFail
            } else {
                _uiEvent.value = EventDetailUiEvent.ScrapFail
            }
        },
        onStart = { canChangeIsScrapped = false },
        onFinish = { canChangeIsScrapped = true },
    )

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_EVENT_ID = -1L
        private const val MEANINGLESS_CODE = -1
    }
}
