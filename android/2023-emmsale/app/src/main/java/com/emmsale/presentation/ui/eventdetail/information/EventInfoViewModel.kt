package com.emmsale.presentation.ui.eventdetail.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.scrap.ScrappedEventRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import kotlinx.coroutines.launch

class EventInfoViewModel(
    private val eventId: Long,
    private val scrappedEventRepository: ScrappedEventRepository,
) : ViewModel() {
    private val _isScraped: MutableLiveData<Boolean> = MutableLiveData(false)
    val isScraped: LiveData<Boolean> = _isScraped

    private val _isError: MutableLiveData<Boolean> = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    init {
        viewModelScope.launch {
            when (val response = scrappedEventRepository.isScraped(eventId)) {
                is ApiSuccess -> _isScraped.value = response.data
                else -> _isError.value = true
            }
        }
    }

    fun scrapEvent() {
        viewModelScope.launch {
            when (scrappedEventRepository.scrapEvent(eventId = eventId)) {
                is ApiSuccess -> _isScraped.value = true
                is ApiError, is ApiException -> _isError.value = true
            }
        }
    }

    fun deleteScrap() {
        viewModelScope.launch {
            when (scrappedEventRepository.deleteScrap(eventId = eventId)) {
                is ApiSuccess -> _isScraped.value = false
                is ApiError, is ApiException -> _isError.value = true
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
