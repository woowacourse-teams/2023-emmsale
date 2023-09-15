package com.emmsale.presentation.ui.eventDetailInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.ScrappedEventRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.eventDetailInfo.uiState.EventInfoUiEvent
import kotlinx.coroutines.launch

class EventInfoViewModel(
    private val eventId: Long,
    private val scrappedEventRepository: ScrappedEventRepository,
) : ViewModel(), Refreshable {
    private val _isScraped: MutableLiveData<Boolean> = MutableLiveData(false)
    val isScraped: LiveData<Boolean> = _isScraped

    private val _isError: MutableLiveData<Boolean> = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _event = MutableLiveData<EventInfoUiEvent?>(null)
    val event: LiveData<EventInfoUiEvent?> = _event

    init {
        refresh()
    }

    override fun refresh() {
        viewModelScope.launch {
            when (val result = scrappedEventRepository.isScraped(eventId)) {
                is Failure, NetworkError -> _isError.value = true
                is Success -> _isScraped.value = result.data
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun scrapEvent() {
        viewModelScope.launch {
            when (val result = scrappedEventRepository.scrapEvent(eventId = eventId)) {
                is Failure, NetworkError -> _event.value = EventInfoUiEvent.SCRAP_ERROR
                is Success -> _isScraped.value = true
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun deleteScrap() {
        viewModelScope.launch {
            when (val result = scrappedEventRepository.deleteScrap(eventId = eventId)) {
                is Failure, NetworkError -> _event.value = EventInfoUiEvent.SCRAP_DELETE_ERROR
                is Success -> _isScraped.value = false
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    companion object {
        fun factory(eventId: Long) = ViewModelFactory {
            EventInfoViewModel(
                eventId = eventId,
                scrappedEventRepository = KerdyApplication.repositoryContainer.scrappedEventRepository,
            )
        }
    }
}
