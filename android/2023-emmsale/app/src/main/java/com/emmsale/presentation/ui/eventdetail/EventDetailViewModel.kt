package com.emmsale.presentation.ui.eventdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.eventdetail.EventDetail
import com.emmsale.data.eventdetail.EventDetailRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.eventdetail.uistate.EventDetailUiState
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val eventId: Long,
    private val eventDetailRepository: EventDetailRepository,
) : ViewModel() {

    init {
        fetchEventDetail(eventId)
    }

    private val _eventDetail: NotNullMutableLiveData<EventDetailUiState> =
        NotNullMutableLiveData(EventDetailUiState())
    val eventDetail: NotNullLiveData<EventDetailUiState>
        get() = _eventDetail

    fun fetchEventDetail(id: Long) {
        setLoadingState(true)
        viewModelScope.launch {
            when (val result = eventDetailRepository.getEventDetail(id)) {
                is ApiSuccess -> fetchSuccessEventDetail(result.data)
                is ApiError -> changeToErrorState()
                is ApiException -> changeToErrorState()
            }
        }
    }

    private fun setLoadingState(state: Boolean) {
        _eventDetail.value = _eventDetail.value.copy(isLoading = state)
    }

    private fun fetchSuccessEventDetail(eventDetail: EventDetail) {
        _eventDetail.value = EventDetailUiState.from(eventDetail)
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
                eventDetailRepository = KerdyApplication.repositoryContainer.eventDetailRepository,
            )
        }
    }
}
