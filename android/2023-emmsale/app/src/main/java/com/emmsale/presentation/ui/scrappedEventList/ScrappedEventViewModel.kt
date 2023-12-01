package com.emmsale.presentation.ui.scrappedEventList

import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.presentation.base.NetworkViewModel
import com.emmsale.presentation.common.ScreenUiState
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.scrappedEventList.uiState.ScrappedEventsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class ScrappedEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : NetworkViewModel() {
    private val _scrappedEvents = NotNullMutableLiveData(ScrappedEventsUiState())
    val scrappedEvents: NotNullLiveData<ScrappedEventsUiState> = _scrappedEvents

    init {
        // NoContentView가 활성화 되지 않기위해 필요
        _screenUiState.value = ScreenUiState.LOADING
    }
    override fun refresh(): Job {
        return refreshData(
            refresh = { eventRepository.getScrappedEvents() },
            onSuccess = { _scrappedEvents.value = ScrappedEventsUiState.from(it) },
        )
    }
}
