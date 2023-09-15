package com.emmsale.presentation.ui.eventDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.model.EventDetail
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.firebase.analytics.logEventClick
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailUiState
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val eventId: Long,
    private val eventRepository: EventRepository,
) : ViewModel(), Refreshable {

    private val _eventDetail: NotNullMutableLiveData<EventDetailUiState> =
        NotNullMutableLiveData(EventDetailUiState())
    val eventDetail: NotNullLiveData<EventDetailUiState> = _eventDetail

    init {
        refresh()
    }

    override fun refresh() {
        setLoadingState(true)
        viewModelScope.launch {
            when (val result = eventRepository.getEventDetail(eventId)) {
                is Success -> fetchSuccessEventDetail(result.data)
                is Failure, NetworkError -> changeToErrorState()
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    private fun setLoadingState(state: Boolean) {
        _eventDetail.value = _eventDetail.value.copy(isLoading = state)
    }

    private fun fetchSuccessEventDetail(eventDetail: EventDetail) {
        _eventDetail.value = EventDetailUiState.from(eventDetail)
        logEventClick(eventDetail.name, eventDetail.id)
    }

    private fun changeToErrorState() {
        _eventDetail.value = _eventDetail.value.copy(
            isError = true,
            isLoading = false,
        )
    }

    companion object {
        fun factory(eventId: Long) = ViewModelFactory {
            EventDetailViewModel(
                eventId,
                eventRepository = KerdyApplication.repositoryContainer.eventRepository,
            )
        }
    }
}
