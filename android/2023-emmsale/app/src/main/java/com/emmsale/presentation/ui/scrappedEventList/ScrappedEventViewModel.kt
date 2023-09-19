package com.emmsale.presentation.ui.scrappedEventList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.repository.interfaces.ScrappedEventRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.scrappedEventList.uiState.ScrappedEventsUiState
import kotlinx.coroutines.launch

class ScrappedEventViewModel(
    private val scrappedEventRepository: ScrappedEventRepository,
) : ViewModel(), Refreshable {
    private val _scrappedEvents = NotNullMutableLiveData(ScrappedEventsUiState())
    val scrappedEvents: NotNullLiveData<ScrappedEventsUiState> = _scrappedEvents

    override fun refresh() {
        changeToLoadingState()
        viewModelScope.launch {
            when (val response = scrappedEventRepository.getScrappedEvents()) {
                is Success -> _scrappedEvents.value = ScrappedEventsUiState.from(response.data)
                else -> changeToErrorState()
            }
        }
    }

    private fun changeToLoadingState() {
        _scrappedEvents.value = ScrappedEventsUiState(isLoading = true)
    }

    private fun changeToErrorState() {
        _scrappedEvents.value =
            ScrappedEventsUiState(isLoading = false, isError = true)
    }

    companion object {
        val factory = ViewModelFactory {
            ScrappedEventViewModel(scrappedEventRepository = KerdyApplication.repositoryContainer.scrappedEventRepository)
        }
    }
}
