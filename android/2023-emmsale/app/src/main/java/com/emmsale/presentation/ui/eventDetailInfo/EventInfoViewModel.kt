package com.emmsale.presentation.ui.eventDetailInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Success
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
            when (val response = scrappedEventRepository.isScraped(eventId)) {
                is Success -> _isScraped.value = response.data
                else -> _isError.value = true
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
