package com.emmsale.presentation.ui.eventdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.eventdetail.EventDetailRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.eventdetail.uistate.EventDetailUiState
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val eventDetailRepository: EventDetailRepository,
) : ViewModel() {

    private val _eventDetail: MutableLiveData<EventDetailUiState> =
        MutableLiveData<EventDetailUiState>()
    val eventDetail: LiveData<EventDetailUiState>
        get() = _eventDetail

    fun fetchEventDetail(id: Long) {
        viewModelScope.launch {
            when (val result = eventDetailRepository.fetchEventDetail(id)) {
                is ApiSuccess -> _eventDetail.postValue(
                    EventDetailUiState.from(result.data),
                )

                is ApiError -> _eventDetail.postValue(EventDetailUiState.Error)
                is ApiException -> _eventDetail.postValue(EventDetailUiState.Error)
            }
        }
    }

    companion object {
        val factory = ViewModelFactory {
            EventDetailViewModel(
                eventDetailRepository = KerdyApplication.repositoryContainer.eventDetailRepository,
            )
        }
    }
}
